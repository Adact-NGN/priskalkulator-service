package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String zoneId;
    
    @Column
    private String postalCode;

    @Column
    private String postalName;

    @Column
    private Boolean isStandardZone;

    @OneToMany()
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "zone_price_row_id", foreignKey = @ForeignKey(name = "Fk_zone_priceRows"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> priceRows;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "zoneId = " + zoneId + ", " +
                "postalCode = " + postalCode + ", " +
                "postalName = " + postalName + ", " +
                "isStandardZone = " + isStandardZone + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Zone zone = (Zone) o;
        return id != null && Objects.equals(id, zone.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
