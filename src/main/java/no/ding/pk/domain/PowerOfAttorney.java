package no.ding.pk.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@Entity
@Table(name = "sale_office_power_of_attorney_matrix")
public class PowerOfAttorney {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private Integer salesOffice;

    @Column
    private String salesOfficeName;

    @Column
    private String region;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_poa_oalvl1"))
    private User ordinaryWasteLvlOneHolder;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_poa_oalvl2"))
    private User ordinaryWasteLvlTwoHolder;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_poa_fa"))
    private User dangerousWasteHolder;
}
