package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.KeyCombination;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import no.ding.pk.repository.bo.KeyCombinationRepository;
import no.ding.pk.web.enums.CustomerType;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

import static no.ding.pk.repository.specifications.TitleTypeSpecification.withTitleType;

@Service
public class BoReportConditionCodeServiceImpl implements BoReportConditionCodeService {

    private static final Logger log = LoggerFactory.getLogger(BoReportConditionCodeServiceImpl.class);

    private final ConditionCodeRepository repository;
    private final KeyCombinationRepository keyCombinationRepository;
    private final KieContainer kieContainer;

    @Autowired
    public BoReportConditionCodeServiceImpl(ConditionCodeRepository repository, KeyCombinationRepository keyCombinationRepository, KieContainer kieContainer) {
        this.repository = repository;
        this.keyCombinationRepository = keyCombinationRepository;
        this.kieContainer = kieContainer;
    }

    @Override
    public List<ConditionCode> getAllConditionCodes(String type) {
        log.debug("Getting list of ConditionCode");
        return repository.findAll(Specification.where(withTitleType(type)));
    }

    @Override
    public ConditionCode save(ConditionCode conditionCode) {
        return repository.save(conditionCode);
    }

    @Override
    public SuggestedConditionCodeKeyCombination getConditionCodeAndKeyCombination(BoReportCondition condition) {
        if(!condition.getHasSalesOrg()) {
            return null;
        }

        return suggestConditionCodeAndKeyCombination(condition);
    }

    @Override
    public Map<String, Map<String, BoReportCondition>> buildBoReportConditionMapForPriceOffer(PriceOffer priceOffer) {

        Map<String, Map<String, BoReportCondition>> boReportConditionMap = new HashMap<>();

        for (SalesOffice salesOffice : priceOffer.getSalesOfficeList()) {
            Map<String, BoReportCondition> soBoReportConditionMap = new HashMap<>();
            createBoReportConditionMap(priceOffer, salesOffice, soBoReportConditionMap, false, salesOffice.getMaterialList());

            createBoReportConditionMap(priceOffer, salesOffice, soBoReportConditionMap, false, salesOffice.getRentalList());

            createBoReportConditionMap(priceOffer, salesOffice, soBoReportConditionMap, false, salesOffice.getTransportServiceList());

            for (Zone zone : salesOffice.getZoneList()) {
                createBoReportConditionMap(priceOffer, salesOffice, soBoReportConditionMap, true, zone.getPriceRows());

            }
            boReportConditionMap.put(salesOffice.getSalesOffice(), soBoReportConditionMap);
        }

        return boReportConditionMap;
    }

    private void createBoReportConditionMap(PriceOffer priceOffer, SalesOffice salesOffice, Map<String, BoReportCondition> soBoReportConditionMap, boolean isZoneMaterial, List<PriceRow> materialList) {
        if(materialList != null) {
            for (PriceRow priceRow : materialList) {
                if (priceRow.getMaterial() != null) {
                    BoReportCondition condition = createBoReportCondition(priceOffer, salesOffice, priceRow, isZoneMaterial);

                    soBoReportConditionMap.put(priceRow.getMaterial().getMaterialNumber(), condition);
                } else {
                    log.debug("No material registered on price row.");
                }
            }
        }
    }

    private BoReportCondition createBoReportCondition(PriceOffer priceOffer, SalesOffice salesOffice,
                                                      PriceRow priceRow, boolean isZoneMaterial) {
        BoReportCondition condition = new BoReportCondition();

        condition.setTerms(priceOffer.getCustomerTerms().getContractTerm());

        setHasSalesOrg(salesOffice, condition);
        setIsPricedOnSalesOffice(priceRow, condition);

        setIsCustomerOrNode(priceOffer, condition);

        condition.setIsZoneMaterial(isZoneMaterial);

        setIsWaste(priceRow, condition);

        setIsService(priceRow, condition);

        setHasDevicePlacement(priceRow, condition);
        condition.setIsDeviceType(StringUtils.isNotBlank(priceRow.getDeviceType()));

        setWasteDisposalMaterial(priceRow, condition);

        setRentalOrProduct(priceRow, condition);
        return condition;
    }

    private void setIsService(PriceRow priceRow, BoReportCondition condition) {
        if(priceRow.getMaterial() == null) {
            log.debug("No material registered on price row.");
            return;
        }
        if(StringUtils.isBlank(priceRow.getMaterial().getDesignation())) {
            condition.setIsService(false);
        }

        condition.setIsService(priceRow.getMaterial().getDesignation().contains("Tjeneste"));
    }

    private void setRentalOrProduct(PriceRow priceRow, BoReportCondition condition) {
        if(priceRow.getMaterial() == null) {
            log.debug("No material registered on price row.");
            return;
        }
        if(StringUtils.isBlank(priceRow.getMaterial().getMaterialGroupDesignation())) {
            condition.setIsRental(false);
            condition.setIsProduct(false);
        } else if(priceRow.getMaterial().getMaterialGroupDesignation().contains("Leie")) {
            condition.setIsRental(true);
            condition.setIsProduct(false);
        } else {
            condition.setIsRental(false);
            condition.setIsProduct(true);
        }
    }

    private void setHasDevicePlacement(PriceRow priceRow, BoReportCondition condition) {
        // TODO: Kommer inn som et satt felt fra BO rapporten.
        condition.setHasDevicePlacement(StringUtils.isNotBlank(priceRow.getDevicePlacement()));
    }

    private void setIsWaste(PriceRow priceRow, BoReportCondition condition) {
        // TODO: Kommer inn som et satt felt fra BO rapporten.
        condition.setIsWaste(false);
    }

    private void setIsCustomerOrNode(PriceOffer priceOffer, BoReportCondition condition) {
        if(StringUtils.equals(priceOffer.getCustomerType(), CustomerType.ORGANIZATION.getType())) {
            condition.setIsCustomer(true);
            condition.setIsNode(false);
        } else {
            condition.setIsCustomer(false);
            condition.setIsNode(true);
        }
    }

    private void setIsPricedOnSalesOffice(PriceRow priceRow, BoReportCondition condition) {
        condition.setIsPricedOnSalesOffice(true);
    }

    private void setHasSalesOrg(SalesOffice salesOffice, BoReportCondition condition) {
        condition.setHasSalesOrg(salesOffice.hasSalesOrg());
    }

    private static void setWasteDisposalMaterial(PriceRow priceRow, BoReportCondition condition) {
        if(priceRow.getMaterial() == null) {
            return;
        }
        if(StringUtils.equals(priceRow.getMaterial().getMaterialTypeDescription(), "Avfallsmateriale")) {
            condition.setIsWasteDisposalMaterial(true);
        }
    }

    @Override
    public SuggestedConditionCodeKeyCombination suggestConditionCodeAndKeyCombination(BoReportCondition condition) {

        log.debug("Received BO condition: {}", condition);

        SuggestedConditionCodeKeyCombination localSuggestion = new SuggestedConditionCodeKeyCombination();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("suggestion", localSuggestion);
        kieSession.insert(condition);
        kieSession.fireAllRules();
        kieSession.dispose();

        return localSuggestion;
    }

    @Override
    public Map<String, Map<String, SuggestedConditionCodeKeyCombination>> getSuggerstionsForPriceOfferBoConditionalMap(Map<String, Map<String, BoReportCondition>> priceOfferBoConditionalMap) {
        Map<String, Map<String, SuggestedConditionCodeKeyCombination>> suggestions = new HashMap<>();

        for (String salesOffice : priceOfferBoConditionalMap.keySet()) {
            Map<String, BoReportCondition> conditionMap = priceOfferBoConditionalMap.get(salesOffice);
            Map<String, SuggestedConditionCodeKeyCombination> materialSuggestionMap = new HashMap<>();

            for (String materialNumber : conditionMap.keySet()) {
                BoReportCondition condition = conditionMap.get(materialNumber);

                SuggestedConditionCodeKeyCombination suggested = suggestConditionCodeAndKeyCombination(condition);

                materialSuggestionMap.put(materialNumber, suggested);
            }

            suggestions.put(salesOffice, materialSuggestionMap);
        }

        return suggestions;
    }

    @Override
    public List<KeyCombination> getKeyCombinationByConditionCode(String conditionCode) {
        Optional<ConditionCode> conditionCodeByCode = repository.findConditionCodeByCode(conditionCode);

        if(conditionCodeByCode.isEmpty()) {
            return new ArrayList<>();
        }
        return conditionCodeByCode.get().getKeyCombinations();
    }

    @Override
    public List<KeyCombination> getKeyCombinationList() {
        return keyCombinationRepository.findAll();
    }
}
