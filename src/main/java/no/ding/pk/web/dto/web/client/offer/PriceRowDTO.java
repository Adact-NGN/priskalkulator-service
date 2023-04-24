package no.ding.pk.web.dto.web.client.offer;


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
    private String material;
    private String materialDesignation;
    private String designation;
    private String productGroupDesignation;
    private String deviceType;
    private Boolean showPriceInOffer;
    private Double manualPrice;
    private Integer priceLevel;
    private Double priceLevelPrice;
    private Double standardPrice;
    private String pricingUnit;
    private String quantumUnit;
    private Integer amount;
    private Double priceIncMva;
    private Date dateUpdated;
    private Date dateCreated;
    private Integer discountLevel;
    private PriceRowDTO combinedMaterials;
    private String categoryId;
    private String categoryDescription;
    private String subCategoryId;
    private String subCategoryDescription;
    private String classId;
    private String classDescription;
}
