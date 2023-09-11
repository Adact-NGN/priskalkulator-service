package no.ding.pk.web.dto.web.client.offer;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

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
    private Double discountLevelPct;
    private Double discountLevelPrice;
    private String material;
    private String designation;
    private String materialDesignation;
    private String productGroupDesignation;
    private String deviceType;
    private String devicePlacement;
    private Boolean showPriceInOffer;
    private Double manualPrice;
    @JsonIgnore
    private Double discountedPrice;
    private Integer priceLevel;
    private Double priceLevelPrice;
    private Double standardPrice;
    private Integer pricingUnit;
    private String quantumUnit;
    private Integer amount;
    private Double priceIncMva;
    private Date dateUpdated;
    private Date dateCreated;
    private Integer discountLevel;
    @JsonProperty(defaultValue = "false")
    private Boolean needsApproval;
    private Boolean approved;
    private PriceRowDTO combinedMaterials;
    private String categoryId;
    private String categoryDescription;
    private String subCategoryId;
    private String subCategoryDescription;
    private String classId;
    private String classDescription;

    @JsonIgnore
    public String getMaterialId() {
        StringBuilder sb = new StringBuilder(material);

        if(StringUtils.isNotBlank(deviceType)) {
            sb.append("_").append(deviceType);
        }

        return sb.toString();
    }

    @JsonIgnore
    @JsonProperty("discountedPrice")
    public void setDiscountedPrice(Double discounted) {
        this.discountedPrice = discounted;
    }

    @JsonIgnore
    @JsonProperty("discountedPrice")
    public Double getDiscountedPrice() {
        if(manualPrice != null) {
            return manualPrice;
        }

        if(standardPrice != null) {
            if(discountLevelPct != null) {
                return standardPrice - (discountLevelPct * 100) / standardPrice;
            }
            if(discountLevelPrice != null) {
                if(discountLevelPrice < 0.0) {
                    return standardPrice + discountLevelPrice;
                } else {
                    return standardPrice - discountLevelPrice;
                }
            }
        }

        return null;
    }
}
