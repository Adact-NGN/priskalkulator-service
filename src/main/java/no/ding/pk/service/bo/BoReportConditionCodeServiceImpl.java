package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static no.ding.pk.repository.specifications.TitleTypeSpecification.withTitleType;

@Service
public class BoReportConditionCodeServiceImpl implements BoReportConditionCodeService {

    private static final Logger log = LoggerFactory.getLogger(BoReportConditionCodeServiceImpl.class);

    private final ConditionCodeRepository repository;
    private final KieContainer kieContainer;

    @Autowired
    public BoReportConditionCodeServiceImpl(ConditionCodeRepository repository, KieContainer kieContainer) {
        this.repository = repository;
        this.kieContainer = kieContainer;
    }

    @Override
    public List<ConditionCode> getAllConditionCodes(String type) {
        log.debug("Getting list of TitleType");
        return repository.findAll(Specification.where(withTitleType(type)));
    }

    @Override
    public ConditionCode save(ConditionCode conditionCode) {
        return repository.save(conditionCode);
    }

    @Override
    public Map<String, String> getConditionCodeAndKeyCombination(BoReportCondition condition) {
        if(!condition.getHasSalesOrg()) {
            return null;
        }

        if(condition.getIsPricedOnSalesOffice()) {
            if(condition.getIsCustomer()) {
                if(condition.getIsNode()) {

                }
            }
        }
        return null;
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
}
