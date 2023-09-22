package no.ding.pk.domain.offer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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

    @Column
    private Boolean isSharable;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "pot_salesOfficeList_id", foreignKey = @ForeignKey(name = "Fk_offer_template_salesOfficeList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SalesOffice> salesOfficeList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pot_salesEmployee_id", foreignKey = @ForeignKey(name = "Fk_offer_template_sales_employee"))
    private User salesEmployee;

    @ManyToOne
    @JoinColumn(name = "pot_approverUser_id", foreignKey = @ForeignKey(name = "Fk_offer_template_approver"))
    private User approver;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "pot_customerTerms_id", foreignKey = @ForeignKey(name = "Fk_offer_template_customerTerms"))
    private PriceOfferTerms customerTerms;

    @Builder(builderMethodName = "priceOfferTemplateBuilder")
    public PriceOfferTemplate(Long id, Boolean deleted, String customerNumber, String customerName, String customerType, List<ContactPerson> contactPersonList, List<SalesOffice> salesOfficeList, User salesEmployee,
                              User approver, Date approvalDate, Date dateIssued, PriceOfferTerms customerTerms) {
        super(id, deleted, customerNumber, customerName, customerType, contactPersonList, approvalDate, dateIssued);

        this.salesOfficeList = salesOfficeList;
        this.salesEmployee = salesEmployee;
        this.approver = approver;
        this.customerTerms = customerTerms;
    }
}


