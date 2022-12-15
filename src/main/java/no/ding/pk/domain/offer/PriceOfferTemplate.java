package no.ding.pk.domain.offer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.User;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "price_offer_template")
public class PriceOfferTemplate extends Offer implements Serializable {
    
    @Builder(builderMethodName = "priceOfferTemplateBuilder")
    public PriceOfferTemplate(Long id, String customerNumber, List<SalesOffice> salesOfficeList, User salesEmployee,
            Boolean needsApproval, User approver, Boolean approved, Date approvalDate, Date dateIssued,
            Terms customerTerms) {
        super(id, customerNumber, salesOfficeList, salesEmployee, needsApproval, approver, approved, approvalDate,
                dateIssued, customerTerms);
    }
}
