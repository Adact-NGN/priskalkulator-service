package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

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
        )
})
@Entity
@Table(name = "materials")
public class Material implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private String scaleQuantum;

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
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Material material = (Material) o;
        return id != null && Objects.equals(id, material.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
