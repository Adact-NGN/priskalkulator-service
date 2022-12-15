package no.ding.pk.domain.offer;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.Auditable;
import no.ding.pk.domain.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class Offer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String customerNumber;

    @OneToMany()
    // @JoinColumn(name = "salesOffice_id")
    private List<SalesOffice> salesOfficeList;

    @OneToOne(optional = false)
    // @JoinColumn(name = "salesEmployee_id")
    private User salesEmployee;

    @Column
    private Boolean needsApproval;

    @OneToOne
    @JoinColumn(name = "approverUser_id")
    private User approver;

    @Column
    private Boolean approved;

    @Column
    private Date approvalDate;

    @Column
    private Date dateIssued;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customerTerms_id")
    private Terms customerTerms;
}
