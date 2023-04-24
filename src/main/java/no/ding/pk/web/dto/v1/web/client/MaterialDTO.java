package no.ding.pk.web.dto.v1.web.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "__metadata" })
public class MaterialDTO {
    private Long id;
    private Date createdDate;
    private Date lastModifiedDate;
    private Date validFrom;
    private Date validTo;
    private Double customerPrice;
    private Double discountPct;
    private Double manualPrice;
    private Double standardPrice;
    private Integer powerOfAttorney;
    private Integer scaleQuantum;
    private MaterialDiscountDTO discountLevel;
    private String createdBy;
    private String currency;
    private String designation;
    private String deviceType;
    private String lastModifiedBy;
    @JsonAlias("materialNumber")
    private String material;
    @JsonAlias("designation")
    private String materialDesignation;
    private String materialGroup;
    private String materialGroupDesignation;
    private String materialType;
    private String priceUnit;
    private String quantumUnit;
    private String salesOffice;
    private String salesOrg;
    @JsonAlias("salesZone")
    private String zone;
}
