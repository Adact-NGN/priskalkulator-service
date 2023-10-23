package no.ding.pk.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Builder(builderMethodName = "hiddenBuilder")
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "discount_matrix")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false, name = "sales_org")
    private String salesOrg;

    @Column(nullable = false, name = "sales_office")
    private String salesOffice;

    @Column(nullable = false, name = "material_number")
    private String materialNumber;
    
    @Column(name = "DEVICE_TYPE")
    private String deviceType;
    
    @Column(name = "material_designation")
    private String materialDesignation;
    
    @Column(name = "standard_price")
    private Double standardPrice;

    @Column
    private String fa;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<DiscountLevel> discountLevels;
    
    public Discount() {
        discountLevels = new ArrayList<>();
    }

    @Builder(builderMethodName = "defaultBuilder")
    public Discount(String salesOrg, String materialNumber, String materialDesignation,
                    double standardPrice) {
        this.salesOrg = salesOrg;
        this.materialNumber = materialNumber;
        this.materialDesignation = materialDesignation;
        this.standardPrice = standardPrice;
        discountLevels = new ArrayList<>();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public void setMaterialDesignation(String materialDesignation) {
        this.materialDesignation = materialDesignation;
    }

    public void setStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
    }
    public void setDiscountLevels(List<DiscountLevel> discountLevelList) {
        this.discountLevels = discountLevelList;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public void addDiscountLevel(DiscountLevel discountLevel) {
        discountLevel.setParent(this);
        
        if(discountLevels == null) {
            discountLevels = new ArrayList<>();
        }
        
        if(standardPrice != null) {
            if(discountLevel.getDiscount() != null && discountLevel.getCalculatedDiscount() == null) {
                discountLevel.setCalculatedDiscount(standardPrice - discountLevel.getDiscount());
                
                double pct = (discountLevel.getDiscount() / standardPrice);
                
                discountLevel.setPctDiscount(pct);
            }
            
            if(discountLevel.getDiscount() == null && discountLevel.getCalculatedDiscount() != null) {
                discountLevel.setDiscount(standardPrice - discountLevel.getCalculatedDiscount());
                
                double pct = (discountLevel.getDiscount() / standardPrice);
                
                discountLevel.setPctDiscount(pct);
            }
        }
        
        discountLevels.add(discountLevel);
    }
    
    @Override
    public String toString() {
        return "Discount [id=" + id + ", salesOrg=" + salesOrg + ", materialNumber=" + materialNumber
        + ", materialDesignation=" + materialDesignation + ", standardPrice="
        + standardPrice + ", discountLevels=" + discountLevels + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((salesOrg == null) ? 0 : salesOrg.hashCode());
        result = prime * result + ((materialNumber == null) ? 0 : materialNumber.hashCode());
        result = prime * result + ((materialDesignation == null) ? 0 : materialDesignation.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Discount other = (Discount) obj;
        if (salesOrg == null) {
            if (other.salesOrg != null)
                return false;
        } else if (!salesOrg.equals(other.salesOrg))
            return false;
        if (materialNumber == null) {
            if (other.materialNumber != null)
                return false;
        } else if (!materialNumber.equals(other.materialNumber))
        return false;
        if (materialDesignation == null) {
            if (other.materialDesignation != null)
                return false;
        } else if (!materialDesignation.equals(other.materialDesignation))
            return false;
        return true;
    }
    
    public static DiscountBuilder builder(String salesOrg, String salesOffice, String materialNumber,
                                                  Double standardPrice, List<DiscountLevel> discountLevels) {
        return Discount.hiddenBuilder().salesOrg(salesOrg).salesOffice(salesOffice).materialNumber(materialNumber)
                .standardPrice(standardPrice).discountLevels(discountLevels);
    }
}
