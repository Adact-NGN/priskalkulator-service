package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.sap.pricing.ConditionRecordDTO;
import no.ding.pk.web.dto.sap.pricing.ConditionRecordValidityDTO;
import no.ding.pk.web.dto.sap.pricing.PricingEntityCombinationMap;
import no.ding.pk.web.dto.sap.pricing.SapCreatePricingEntitiesRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sap-pricing")
public class SapCreatePricingEntitiesController {

    private static final Logger log = LoggerFactory.getLogger(SapCreatePricingEntitiesRequest.class);

    private PriceOfferService priceOfferService;

    @PostMapping(path = "/create")
    public Boolean createNewSapPricingEntities(@RequestBody SapCreatePricingEntitiesRequest pricingEntitiesRequest) {
        if(pricingEntitiesRequest.getPriceOfferId() == null) {
            log.debug("No price offer id provided");
            throw new RuntimeException("No price offer id provided");
        }

        Optional<PriceOffer> priceOfferOptional = priceOfferService.findById(pricingEntitiesRequest.getPriceOfferId());

        if(priceOfferOptional.isEmpty()) {
            String errorMessage = String.format("No price offer with id %d, found.", pricingEntitiesRequest.getPriceOfferId());
            log.debug(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        PriceOffer priceOffer = priceOfferOptional.get();

        if(StringUtils.isNotBlank(pricingEntitiesRequest.getCustomerNumber())) {
            priceOfferService.updateCustomerNumber(priceOffer.getId(), pricingEntitiesRequest.getCustomerNumber());
        }

        Map<String, PriceRow> materialPriceRowMap = createMaterialPriceRowMap(priceOffer);

        for (PricingEntityCombinationMap combinationMap : pricingEntitiesRequest.getPricingEntityCombinationMaps()) {

            String lookupKey = combinationMap.getMaterialId();
            PriceRow priceRow = materialPriceRowMap.get(lookupKey);

            ConditionRecordDTO conditionRecordDTO = ConditionRecordDTO.builder(
                    combinationMap.getKeyCombinationTableName(),
                    "C",
                    combinationMap.getConditionCode(),
                    priceRow.getDiscountLevelPrice(),
                    priceRow.get)
        }
    }

    private Map<String, PriceRow> createMaterialPriceRowMap(PriceOffer priceOffer) {

        Map<String, PriceRow> materialPriceRowMap = new HashMap<>();

        for (SalesOffice salesOffice : priceOffer.getSalesOfficeList()) {

            for (PriceRow priceRow : salesOffice.getMaterialList()) {
                String sb = salesOffice.getSalesOrg() + "_" + salesOffice.getSalesOffice() + "_" + priceRow.getMaterialId();
                materialPriceRowMap.put(sb, priceRow);
            }
        }

        return null;
    }
}
