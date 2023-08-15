package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;

import java.util.List;
import java.util.Map;

public interface BoReportConditionCodeService {

    List<ConditionCode> getAllConditionCodes(String type);

    ConditionCode save(ConditionCode conditionCode);

    Map<String, String> getConditionCodeAndKeyCombination(BoReportCondition condtion);

    SuggestedConditionCodeKeyCombination suggestConditionCodeAndKeyCombination(BoReportCondition condition);
}
