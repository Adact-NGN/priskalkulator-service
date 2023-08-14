package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;

import java.util.List;
import java.util.Map;

public interface BoReportTitleTypeService {

    List<ConditionCode> getAllTitleTypes(String type);

    ConditionCode save(ConditionCode conditionCode);

    Map<String, String> getConditionCodeAndKeyCombination(BoReportCondition condtion);

    Map<String, String> suggestConditionCodeAndKeyCombination(BoReportCondition condition,
                                                                     SuggestedConditionCodeKeyCombination suggestion);
}
