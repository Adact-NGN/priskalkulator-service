package no.ding.pk.domain.offer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "po_salesOfficeList_id", foreignKey = @ForeignKey(name = "Fk_price_offer_salesOfficeList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SalesOffice> salesOfficeList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "po_salesEmployee_id", foreignKey = @ForeignKey(name = "Fk_price_offer_sales_employee"))
    private User salesEmployee;

    @ManyToOne
    @JoinColumn(name = "po_approverUser_id", foreignKey = @ForeignKey(name = "Fk_price_offer_approver"))
    private User approver;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "po_customerTerms_id", foreignKey = @ForeignKey(name = "Fk_price_offer_customerTerms"))
    private PriceOfferTerms customerTerms;

    @Builder(builderMethodName = "priceOfferBuilder")
    public PriceOffer(Long id, String customerNumber, String customerName, List<SalesOffice> salesOfficeList, User salesEmployee,
            Boolean needsApproval, User approver, Boolean approved, Date approvalDate, Date dateIssued, PriceOfferTerms priceOfferTerms) {
        super(id, customerNumber, customerName, needsApproval, approved, approvalDate,
                dateIssued);

        this.salesOfficeList = salesOfficeList;
        this.salesEmployee = salesEmployee;
        this.approver = approver;
        this.customerTerms = priceOfferTerms;
    }
}
