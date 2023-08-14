package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.ding.pk.repository.specifications.TitleTypeSpecification.withTitleType;

@Service
public class BoReportTitleTypeServiceImpl implements BoReportTitleTypeService {

    private static final Logger log = LoggerFactory.getLogger(BoReportTitleTypeServiceImpl.class);

    private final ConditionCodeRepository repository;
    private final KieSession kieSession;

    @Autowired
    public BoReportTitleTypeServiceImpl(ConditionCodeRepository repository, KieSession kieSession) {
        this.repository = repository;
        this.kieSession = kieSession;
    }

    @Override
    public List<ConditionCode> getAllTitleTypes(String type) {
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

    public Map<String, String> suggestConditionCodeAndKeyCombination(BoReportCondition condition,
                                                                     SuggestedConditionCodeKeyCombination suggestion) {
        String suggestedConditionCode = "";
        String suggestedKeyCombination = "";
        try {
            kieSession.insert(condition);
            kieSession.setGlobal("suggestConditionCode", suggestedConditionCode);
            kieSession.setGlobal("suggestedKeyCombination", suggestedKeyCombination);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        Map<String, String> suggestedConditionCodeAndKeyCombination = new HashMap<>();
        suggestedConditionCodeAndKeyCombination.put(suggestedConditionCode, suggestedKeyCombination);
        return suggestedConditionCodeAndKeyCombination;
    }
}
