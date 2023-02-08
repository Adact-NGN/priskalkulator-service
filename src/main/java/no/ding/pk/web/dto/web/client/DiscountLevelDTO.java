package no.ding.pk.web.dto.web.client;

import lombok.Data;

@Data
public class DiscountLevelDTO {
    private Long id;
    private String level;
    private Double discount;
    private String calculatedDiscount;
    private String pctDiscount;
}
