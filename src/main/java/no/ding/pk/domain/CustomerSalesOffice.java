package no.ding.pk.domain;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(builderMethodName = "hiddenBuilder")
@Entity
@Table(name = "customer_sales_office")
public class CustomerSalesOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String salesOrg;

    @Column
    private String salesOffice;

    @Column
    private String salesOfficeName;

    @Column
    private String region;

    @OneToOne()
    private PowerOfAttorney powerOfAttorney;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static CustomerSalesOfficeBuilder builder() {
        return hiddenBuilder();
    }
}
