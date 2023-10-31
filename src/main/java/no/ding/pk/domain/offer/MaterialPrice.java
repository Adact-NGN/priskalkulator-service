package no.ding.pk.domain.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import no.ding.pk.domain.Auditable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "material_price", uniqueConstraints = @UniqueConstraint(
        name = "material_price_identifier",
        columnNames = {"materialNumber", "deviceType", "zone"}))
public class MaterialPrice extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String materialNumber;

    @Column
    private String deviceType;

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

    @Column
    private String zone;

    @JsonIgnore
    public String getUniqueMaterialNumber() {
        StringBuilder sb = new StringBuilder(materialNumber);

        if(StringUtils.isNotBlank(deviceType)) {
            sb.append("_").append(deviceType);
        }

        if(StringUtils.isNotBlank(zone)) {
            sb.append("_").append(zone);
        }

        return sb.toString();
    }

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
                .append(quantumUnit, that.quantumUnit).append(zone, that.zone).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(materialNumber)
                .append(standardPrice).append(validFrom).append(validTo).append(pricingUnit)
                .append(quantumUnit).append(zone).toHashCode();
    }
}
