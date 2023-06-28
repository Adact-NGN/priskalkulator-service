package no.ding.pk.domain.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(
                name = "findMaterialByMaterialNumber",
                query = "from Material m where m.materialNumber = :materialNumber"
        ),
        @NamedQuery(
                name = "findMaterialByMaterialNumberAndDeviceType",
                query = "from Material m where m.materialNumber = :materialNumber and m.deviceType = :deviceType"
        )
})
@JsonIgnoreProperties({"faMaterial"})
@Entity
@Table(name = "materials")
public class Material implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String currency;

    @Column
    private String designation;

    @Column
    private String deviceType;

    @Column(unique = true, nullable = false)
    private String materialNumber;

    @Column
    private String salesOrg;

    @Column
    private String salesOffice;

    @Column
    private String materialGroup;

    @Column
    private String materialGroupDesignation;

    @Column
    private String materialType;

    @Column
    private String materialTypeDescription;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(name = "Fk_material_materialStdPrice"))
    private MaterialPrice materialStandardPrice;

    @Column
    private Integer pricingUnit;

    @Column
    private String quantumUnit; // KG, STK etc.

    @Column
    private Double scaleQuantum;

    @Column
    private String salesZone;

    @Column
    private String categoryId; // "
    @Column
    private String categoryDescription; // "
    @Column
    private String subCategoryId; // "
    @Column
    private String subCategoryDescription; // "
    @Column
    private String classId; // "
    @Column
    private String classDescription; // "

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Material material = (Material) o;

        return new EqualsBuilder().append(id, material.id).append(currency, material.currency)
                .append(designation, material.designation).append(deviceType, material.deviceType)
                .append(materialNumber, material.materialNumber).append(salesOrg, material.salesOrg)
                .append(materialGroup, material.materialGroup)
                .append(materialGroupDesignation, material.materialGroupDesignation)
                .append(materialType, material.materialType)
                .append(materialTypeDescription, material.materialTypeDescription)
                .append(materialStandardPrice, material.materialStandardPrice)
                .append(pricingUnit, material.pricingUnit).append(quantumUnit, material.quantumUnit)
                .append(scaleQuantum, material.scaleQuantum).append(salesZone, material.salesZone)
                .append(categoryId, material.categoryId).append(categoryDescription, material.categoryDescription)
                .append(subCategoryId, material.subCategoryId)
                .append(subCategoryDescription, material.subCategoryDescription)
                .append(classId, material.classId).append(classDescription, material.classDescription)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(currency)
                .append(designation).append(deviceType).append(materialNumber).append(materialGroup)
                .append(materialGroupDesignation).append(materialType).append(materialTypeDescription)
                .append(materialStandardPrice).append(pricingUnit).append(quantumUnit).append(scaleQuantum)
                .append(salesZone).append(categoryId).append(categoryDescription).append(subCategoryId)
                .append(subCategoryDescription).append(classId).append(classDescription)
                .toHashCode();
    }

    public boolean isFaMaterial() {
        return categoryDescription != null && categoryDescription.equals("Farlig avfall");
    }
}
