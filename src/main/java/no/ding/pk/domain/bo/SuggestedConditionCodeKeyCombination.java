package no.ding.pk.domain.bo;

import lombok.Data;

@Data
public class SuggestedConditionCodeKeyCombination {
    private String conditionCode;
    private String keyCombination;
    private String keyCombinationTableName;
}
