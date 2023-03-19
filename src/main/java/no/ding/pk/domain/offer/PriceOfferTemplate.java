package no.ding.pk.domain.offer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "price_offer_template")
public class PriceOfferTemplate extends Offer implements Serializable {
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "pot_salesOfficeList_id", foreignKey = @ForeignKey(name = "Fk_offer_template_salesOfficeList"))
    private List<SalesOffice> salesOfficeList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pot_salesEmployee_id", foreignKey = @ForeignKey(name = "Fk_offer_template_sales_employee"))
    private User salesEmployee;

    @ManyToOne
    @JoinColumn(name = "pot_approverUser_id", foreignKey = @ForeignKey(name = "Fk_offer_template_approver"))
    private User approver;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "pot_customerTerms_id", foreignKey = @ForeignKey(name = "Fk_offer_template_customerTerms"))
    private Terms customerTerms;

    @Builder(builderMethodName = "priceOfferTemplateBuilder")
    public PriceOfferTemplate(Long id, String customerNumber, String customerName, List<SalesOffice> salesOfficeList, User salesEmployee,
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


