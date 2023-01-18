package no.ding.pk.domain.offer;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.*;
import no.ding.pk.domain.Auditable;
import no.ding.pk.domain.User;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@MappedSuperclass
public class Offer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String customerNumber;

    @Column
    private String customerName;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<SalesOffice> salesOfficeList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "salesEmployeeId")
    private User salesEmployee;

    @Column
    private Boolean needsApproval;

    @ManyToOne
    @JoinColumn(name = "approverUser_id")
    private User approver;

    @Column
    private Boolean isApproved;

    @Column
    private Date approvalDate;

    @Column
    private Date dateIssued;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customerTerms_id")
    private Terms customerTerms;
}
