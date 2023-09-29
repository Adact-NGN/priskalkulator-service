package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.Auditable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class Offer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean deleted = false;

    @Column
    private String customerNumber;

    @Column
    private String customerName;

    @Column
    private String customerType;

    @Column
    private String streetAddress;

    @Column
    private String postalNumber;

    @Column
    private String city;

    @Column
    private String organizationNumber;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ContactPerson> contactPersonList;

    @Column
    private Date approvalDate;

    @Column
    private Date dateIssued;

    @Column
    public List<ContactPerson> getContactPersonList() {
        return contactPersonList;
    }

    public void setContactPersonList(List<ContactPerson> contactPersonList) {
        if(this.contactPersonList == null) {
            this.contactPersonList = new ArrayList<>();
        }

        this.contactPersonList.clear();
        if(contactPersonList != null && !contactPersonList.isEmpty()) {
            this.contactPersonList.addAll(contactPersonList);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "deleted = " + deleted + ", " +
                "createdBy = " + createdBy + ", " +
                "createdDate = " + createdDate + ", " +
                "lastModifiedBy = " + lastModifiedBy + ", " +
                "lastModifiedDate = " + lastModifiedDate + ", " +
                "customerNumber = " + customerNumber + ", " +
                "customerName = " + customerName + ", " +
                "approvalDate = " + approvalDate + ", " +
                "dateIssued = " + dateIssued + ")";
    }
}
