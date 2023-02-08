package no.ding.pk.web.dto.web.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PriceRowDTO {
    private Long id;
    private Double customerPrice;
    private Double discountPct;
    private MaterialDTO material;
    private boolean showPriceInOffer;
    private Double manualPrice;
    private PriceLevelDTO priceLevel;
    private Double priceLevelPrice;
    private Double standardPrice;
    private Integer amount;
    private Double priceIncMva;
    private Date dateUpdated;
    private Date dateCreated;
    private DiscountLevelDTO discountLevel;
    private PriceRowDTO combinedMaterials;
}
