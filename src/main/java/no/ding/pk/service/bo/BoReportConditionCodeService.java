package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.domain.offer.PriceOffer;

import java.util.List;
import java.util.Map;

public interface BoReportConditionCodeService {

    List<ConditionCode> getAllConditionCodes(String type);

    ConditionCode save(ConditionCode conditionCode);

    SuggestedConditionCodeKeyCombination getConditionCodeAndKeyCombination(BoReportCondition condtion);

    Map<String, Map<String, BoReportCondition>> buildBoReportConditionMapForPriceOffer(PriceOffer priceOffer);

    SuggestedConditionCodeKeyCombination suggestConditionCodeAndKeyCombination(BoReportCondition condition);

    Map<String, Map<String, SuggestedConditionCodeKeyCombination>> getSuggerstionsForPriceOfferBoConditionalMap(Map<String, Map<String, BoReportCondition>> priceOfferBoConditionalMap);
}
