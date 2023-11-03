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
@Builder(builderMethodName = "hiddenBuilder")
@Entity
@Table(name = "material_price", uniqueConstraints = @UniqueConstraint(
        name = "material_price_identifier",
        columnNames = {"salesOrg", "salesOffice", "materialNumber", "deviceType", "zone"}))
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
    private String salesOrg;

    @Column
    private String salesOffice;

    @Column
    private String zone;

    @JsonIgnore
    public String getUniqueMaterialNumber() {
        StringBuilder sb = new StringBuilder();

        if(StringUtils.isNotBlank(salesOrg)) {
            sb.append(salesOrg);
        }

        if(StringUtils.isNotBlank(salesOffice)) {
            if(!sb.isEmpty()) {
                sb.append("_");
            }

            sb.append(salesOffice);
        }

        if(StringUtils.isNotBlank(materialNumber)) {
            if(!sb.isEmpty()) {
                sb.append("_");
            }

            sb.append(materialNumber);
        }

        if(StringUtils.isNotBlank(deviceType)) {
            sb.append("_").append(deviceType);
        }

        if(StringUtils.isNotBlank(zone)) {
            String salesZone = String.format("0%d", Integer.valueOf(zone)) ;
            sb.append("_").append(salesZone);
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

        return new EqualsBuilder().append(materialNumber, that.materialNumber)
                .append(standardPrice, that.standardPrice).append(validFrom, that.validFrom)
                .append(validTo, that.validTo).append(pricingUnit, that.pricingUnit)
                .append(quantumUnit, that.quantumUnit).append(salesOrg, that.salesOrg)
                .append(salesOffice, that.salesOffice)
                .append(zone, that.zone).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(materialNumber)
                .append(standardPrice).append(validFrom).append(validTo).append(pricingUnit)
                .append(quantumUnit).append(salesOrg).append(salesOffice).append(zone).toHashCode();
    }

    public static MaterialPriceBuilder builder(String salesOrg, String salesOffice, String materialNumber, String deviceType, String zone) {
        return MaterialPrice.hiddenBuilder().salesOrg(salesOrg)
                .salesOffice(salesOffice)
                .materialNumber(materialNumber)
                .deviceType(deviceType)
                .zone(zone);
    }
}
