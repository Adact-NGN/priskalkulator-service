package no.ding.pk.web.dto.v1.bo;

import lombok.Data;

import java.util.List;

@Data
public class TitleTypeDTO {
    private String titleType;
    private List<KeyCombinationDTO> keyCombinations;
}
