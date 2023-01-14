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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "materials")
public class Material {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String materialNumber;

    @Column
    private String designation;

    @Column
    private String materialGroup;

    @Column
    private String materialGroupDesignation;

    @Column
    private String materialType;

    @Column
    private String materialTypeDescription;

    @Column
    private String deviceType;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private MaterialPrice materialStandardPrice;

    @Column
    private String currency;

    @Column
    private Integer priceUnit;

    @Column
    private String quantumUnit; // KG, STK etc.

    @Column
    private String salesZone;

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
