package no.ding.pk.web.dto.v1.bo;

import lombok.Data;

@Data
public class BoKeyCodeSuggestionDTO {
    private String conditionCode;
    private String keyCombination;
    private String keyCombinationTableName;
}
