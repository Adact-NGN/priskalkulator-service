package no.ding.pk.web.dto.web.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesOfficePowerOfAttorneyDTO {
    private Long id;
    private String salesOffice;
    private String saleOfficeName;
    private String region;
    private String mailOrdinaryWasteLvlOne;
    private String mailOrdinaryWasteLvlTwo;
    private String dangerousWaste;
}
