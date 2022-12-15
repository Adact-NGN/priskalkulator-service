package no.ding.pk.domain.offer;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String name;

    @Column
    private String postalNumber;

    @Column
    private String city;

    @OneToMany
    private List<PriceRow> materialList;

    @OneToMany
    private List<Zone> zoneList;

    @OneToMany
    private List<PriceRow> transportServiceList;

    @OneToMany
    private List<PriceRow> rentalList;
}
