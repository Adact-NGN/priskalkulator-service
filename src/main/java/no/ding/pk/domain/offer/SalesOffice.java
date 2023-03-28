package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sales_office")
public class SalesOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column
    private String customerNumber;

    @Column
    private String salesOrg;

    @Column
    private String salesOffice;

    @Column
    private String salesOfficeName;

    @Column
    private String postalNumber;

    @Column
    private String city;

    @OneToMany
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "material_list_id", foreignKey = @ForeignKey(name = "Fk_salesOffice_materialList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> materialList;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "zones_id", foreignKey = @ForeignKey(name = "Fk_salesOffice_zone"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Zone> zones;

    @OneToMany()
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "transport_list_id", foreignKey = @ForeignKey(name ="Fk_salesOffice_transportServiceList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> transportServiceList;

    @OneToMany()
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "rental_list_id", foreignKey = @ForeignKey(name = "Fk_salesOffice_rentalList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> rentalList;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "customerNumber = " + customerNumber + ", " +
                "salesOrg = " + salesOrg + ", " +
                "salesOffice = " + salesOffice + ", " +
                "salesOfficeName = " + salesOfficeName + ", " +
                "postalNumber = " + postalNumber + ", " +
                "city = " + city + ")";
    }
}
