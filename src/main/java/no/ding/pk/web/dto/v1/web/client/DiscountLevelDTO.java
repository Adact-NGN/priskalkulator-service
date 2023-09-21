package no.ding.pk.web.dto.v1.web.client;

import lombok.Data;

@Data
public class DiscountLevelDTO {
    private Long id;
    private String level;
    private Double discount;
    private String calculatedDiscount;
    private String pctDiscount;
    private Integer zone;
}
