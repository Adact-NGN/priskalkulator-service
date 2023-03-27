package no.ding.pk.domain.offer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


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
    private Terms customerTerms;

    @Builder(builderMethodName = "priceOfferBuilder")
    public PriceOffer(Long id, String customerNumber, String customerName, List<SalesOffice> salesOfficeList, User salesEmployee,
            Boolean needsApproval, User approver, Boolean approved, Date approvalDate, Date dateIssued,
            Terms customerTerms) {
        super(id, customerNumber, customerName, needsApproval, approved, approvalDate,
                dateIssued);

        this.salesOfficeList = salesOfficeList;
        this.salesEmployee = salesEmployee;
        this.approver = approver;
        this.customerTerms = customerTerms;
    }
}
