package no.ding.pk.domain.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.Auditable;
import org.apache.commons.lang3.StringUtils;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ContactPerson> contactPersonList;

    @Column
    private Date approvalDate;

    @Column
    private Date dateIssued;

    public void setContactPersonList(List<ContactPerson> tempContactPersonList) {
        if(this.contactPersonList == null) {
            this.contactPersonList = new ArrayList<>();
        }

        for(ContactPerson contactPerson : tempContactPersonList) {
            if(!this.contactPersonList.contains(contactPerson)) {
                this.contactPersonList.add(contactPerson);
            }
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

    @JsonIgnore
    public boolean isNodeCustomer() {
        return !StringUtils.isBlank(customerType) && StringUtils.equals(customerType, "Node");
    }
}
