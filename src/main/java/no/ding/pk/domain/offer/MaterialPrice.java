package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.Auditable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "material_price")
public class MaterialPrice extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String materialNumber;

    @Column
    private Double standardPrice;

    @Column
    private Date validFrom;

    @Column
    private Date validTo;

    @Column
    private Integer pricingUnit;

    @Column
    private String quantumUnit;

    public void copy(MaterialPrice materialStandardPrice) {
        this.standardPrice = materialStandardPrice.getStandardPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MaterialPrice that = (MaterialPrice) o;

        return new EqualsBuilder().append(id, that.id).append(materialNumber, that.materialNumber)
                .append(standardPrice, that.standardPrice).append(validFrom, that.validFrom)
                .append(validTo, that.validTo).append(pricingUnit, that.pricingUnit)
                .append(quantumUnit, that.quantumUnit).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(materialNumber)
                .append(standardPrice).append(validFrom).append(validTo).append(pricingUnit)
                .append(quantumUnit).toHashCode();
    }
}
