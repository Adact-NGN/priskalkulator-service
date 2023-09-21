package no.ding.pk.domain.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String customerNumber;

    @Column
    private String company;

    @Column
    private String distributionChannel;

    @Column
    private String name1;

    @Column
    private String name2;

    @Column
    private String organizationNumber;

    @Column
    private String customerGroup;

    @Column
    private String customerType;

    @Column
    private String motherCompany;

    @Column
    private String payer;

    @Column
    private String streetAddress;

    @Column
    private String houseNumber;

    @Column
    private String postalNumber;

    @Column
    private String city;  //: "Oslo",

    @Column
    private String country; //: "NO",

    @Column
    private String region;

    @Column
    private String timeZone; //: "CET",

    @Column
    private String regStrGrp;

    @Column
    private String postBox;

    @Column
    private String postBoxNumber;

    @Column
    private String firmPostalNumber;

    @Column
    private String creditScore;  //: "",

    @Column
    private Date changedDate; //: null,

    @Column
    private String changedTime;//: "00:00:00",

    @Column
    private String abcClassification;

    @OneToMany
    private List<SapContactPerson> sapContactPeople;

    @OneToMany
    private List<CustomerBranch> customerBranchList;

    @OneToMany
    private List<NodeCustomer> nodeCustomerList;
}
