package no.ding.pk.domain.offer;

import lombok.*;
import no.ding.pk.domain.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "price_offers")
public class PriceOffer extends Offer implements Serializable {

    @Column
    private String priceOfferStatus;

    @Column(length = 21844)
    private String materialsForApproval;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "po_salesOfficeList_id", foreignKey = @ForeignKey(name = "Fk_price_offer_salesOfficeList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SalesOffice> salesOfficeList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "po_salesEmployee_id", foreignKey = @ForeignKey(name = "Fk_price_offer_sales_employee"))
    private User salesEmployee;

    @ManyToOne
    @JoinColumn(name = "po_approverUser_id", foreignKey = @ForeignKey(name = "Fk_price_offer_approver"))
    private User approver;

    @Column
    private Date approvalDate;

    @Column
    private Boolean needsApproval;

    @Column
    private String generalComment;

    @Column
    private String additionalInformation; // Godkjenning/Avslagg

    @Column
    private Date activationDate;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "po_customerTerms_id", foreignKey = @ForeignKey(name = "Fk_price_offer_customerTerms"))
    private PriceOfferTerms customerTerms;

    @Builder(builderMethodName = "priceOfferBuilder")
    public PriceOffer(Long id, Boolean deleted, String customerNumber, String customerName, String customerType,
                      String streetAddress,
                      String postalNumber,
                      String city,
                      List<SalesOffice> salesOfficeList, User salesEmployee,
                      Boolean needsApproval, User approver, Date approvalDate, Date dateIssued, PriceOfferTerms priceOfferTerms,
                      String priceOfferStatus, Date activationDate, List<ContactPerson> contactPersonList) {
        super(id, deleted, customerNumber, customerName, customerType,
                streetAddress,
                postalNumber,
                city,
                contactPersonList, approvalDate,
                dateIssued);

        this.needsApproval = needsApproval;
        this.salesOfficeList = salesOfficeList;
        this.salesEmployee = salesEmployee;
        this.approver = approver;
        this.customerTerms = priceOfferTerms;
        this.priceOfferStatus = priceOfferStatus;
        this.approvalDate = approvalDate;
        this.activationDate = activationDate;
    }

    public Boolean getNeedsApproval() {
        return needsApproval != null && needsApproval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PriceOffer that = (PriceOffer) o;

        return new EqualsBuilder().append(priceOfferStatus, that.priceOfferStatus).append(salesOfficeList, that.salesOfficeList).append(salesEmployee, that.salesEmployee).append(approver, that.approver).append(approvalDate, that.approvalDate).append(activationDate, that.activationDate).append(customerTerms, that.customerTerms).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(priceOfferStatus).append(salesOfficeList).append(salesEmployee).append(approver).append(approvalDate).append(activationDate).append(customerTerms).toHashCode();
    }
}
