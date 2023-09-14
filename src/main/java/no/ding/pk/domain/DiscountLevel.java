package no.ding.pk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discount_levels")
public class DiscountLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int level;

    @Column
    private Double discount;

    @Column
    private Double calculatedDiscount;

    @Column
    private Double pctDiscount;

    @Column
    private Integer zone;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="discount_id", nullable = false)
    private Discount parent;
    
    public DiscountLevel(int level, Double discount, Double calculatedDiscount, Double pctDiscount) {
        this.level = level;
        this.discount = discount;
        this.calculatedDiscount = calculatedDiscount;
        this.pctDiscount = pctDiscount;
    }


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }   

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public Double getDiscount() {
        return discount;
    }
    public void setDiscount(Double discount) {
        this.discount = discount;
    }
    public Double getCalculatedDiscount() {
        return calculatedDiscount;
    }
    public void setCalculatedDiscount(Double calculatedDiscount) {
        this.calculatedDiscount = calculatedDiscount;
    }
    public Double getPctDiscount() {
        return pctDiscount;
    }
    public void setPctDiscount(Double pctDiscount) {
        this.pctDiscount = pctDiscount;
    }
    
    public Discount getParent() {
        return parent;
    }
    public void setParent(Discount parent) {
        this.parent = parent;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        return "DiscountLevel [id=" + id + ", level=" + level + ", discount=" + discount + ", calculatedDiscount="
                + calculatedDiscount + ", pctDiscount=" + pctDiscount + ", zone=" + zone + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + level;
        long temp;
        temp = Double.doubleToLongBits(discount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(calculatedDiscount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pctDiscount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        DiscountLevel other = (DiscountLevel) obj;
        if (level != other.level)
            return false;
        if (Double.doubleToLongBits(discount) != Double.doubleToLongBits(other.discount))
            return false;
        if (Double.doubleToLongBits(calculatedDiscount) != Double.doubleToLongBits(other.calculatedDiscount))
            return false;
        if (Double.doubleToLongBits(pctDiscount) != Double.doubleToLongBits(other.pctDiscount))
            return false;
        return true;
    }


}
