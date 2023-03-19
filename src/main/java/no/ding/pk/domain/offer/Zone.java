package no.ding.pk.domain.offer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}
