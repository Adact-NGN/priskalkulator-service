package no.ding.pk.web.dto.web.client;

import lombok.Data;

@Data
public class PriceLevelDTO {
    private Long id;
    private Integer level;
    private Double discount;
    private Double calculatedDiscount;
    private Double pctDiscount;
}
