package no.ding.pk.web.dto.v1.bo;

import lombok.Data;

import java.util.List;

@Data
public class ConditionCodeDTO {
    private String code;
    private List<KeyCombinationDTO> keyCombinations;
    private String priceType;
}
