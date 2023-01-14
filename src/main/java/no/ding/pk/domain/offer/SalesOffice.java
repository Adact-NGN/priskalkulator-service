package no.ding.pk.domain.offer;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Getter
@Setter
@ToString
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

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "material_list_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> materialList;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Zone> zones;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transport_list_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> transportServiceList;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "rental_list_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> rentalList;
}
