package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private Long id;
    private String currency;
    private String designation;
    private String deviceType;
    private String material;
    private String materialDesignation;
    private String materialType;
    private String priceUnit;
    private String materialGroup;
    private String materialGroupDesignation;
    private String quantumUnit;
    private String salesOffice;
    private String salesOrg;
    private Integer scaleQuantum;
    private Double standardPrice;
    private Date validFrom;
    private Date validTo;
    private String zone;
    private Integer powerOfAttorney;
    private Double manualPrice;
    private Double customerPrice;
    private MaterialDiscountDTO selectedDiscount;
}
