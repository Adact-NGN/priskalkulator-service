package no.ding.pk.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "discount_matrix")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String salesOrg;

    @Column
    private String materialNumber;

    @Column
    private String materialDesignation;

    @Column
    private double standardPrice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private Set<DiscountLevel> discountLevels;
    
    public Discount() {
    }
    
    public Discount(String salesOrg, String materialNumber, String materialDesignation, String salesOffice,
            double standardPrice) {
        this.salesOrg = salesOrg;
        this.materialNumber = materialNumber;
        this.materialDesignation = materialDesignation;
        this.standardPrice = standardPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSalesOrg() {
        return salesOrg;
    }
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    public String getMaterialNumber() {
        return materialNumber;
    }
    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }
    public String getMaterialDesignation() {
        return materialDesignation;
    }
    public void setMaterialDesignation(String materialDesignation) {
        this.materialDesignation = materialDesignation;
    }
    public double getStandardPrice() {
        return standardPrice;
    }
    public void setStandardPrice(double standardPrice) {
        this.standardPrice = standardPrice;
    }
    public Set<DiscountLevel> getDiscountLevelList() {
        return discountLevels;
    }
    public void setDiscountLevels(Set<DiscountLevel> discountLevelList) {
        this.discountLevels = discountLevelList;
    }

    public void addDiscountLevel(DiscountLevel discountLevel) {
        discountLevel.setParent(this);

        if(discountLevels == null) {
            discountLevels = new HashSet<>();
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
}
