package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesOfficePowerOfAttorneyDTO {
    private Long id;
    private Integer salesOffice;
    private String salesOfficeName;
    private String region;
    private String mailOrdinaryWasteLvlOne;
    private String mailOrdinaryWasteLvlTwo;
    private String dangerousWaste;
}
