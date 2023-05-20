package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.Auditable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
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

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ContactPerson> contactPersonList;

    @Column
    private Boolean needsApproval;

    @Column
    private Boolean isApproved;

    @Column
    private String dismissalReason;

    @Column
    private Date approvalDate;

    @Column
    private Date dateIssued;

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

    public Boolean getNeedsApproval() {
        return needsApproval != null && needsApproval;
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
                "needsApproval = " + needsApproval + ", " +
                "isApproved = " + isApproved + ", " +
                "approvalDate = " + approvalDate + ", " +
                "dateIssued = " + dateIssued + ")";
    }
}
