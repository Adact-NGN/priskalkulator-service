package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDiscountDTO {
    private Long id;
    private String level;
    private Double discount;
    private String calculatedDiscount;
    private String pctDiscount;
}
