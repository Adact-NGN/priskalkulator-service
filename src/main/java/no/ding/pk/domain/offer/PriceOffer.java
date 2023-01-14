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
import lombok.ToString;
import no.ding.pk.domain.User;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "price_offers")
public class PriceOffer extends Offer implements Serializable {
    

    @Builder(builderMethodName = "priceOfferBuilder")
    public PriceOffer(Long id, String customerNumber, String customerName, List<SalesOffice> salesOfficeList, User salesEmployee,
            Boolean needsApproval, User approver, Boolean approved, Date approvalDate, Date dateIssued,
            Terms customerTerms) {
        super(id, customerNumber, customerName, salesOfficeList, salesEmployee, needsApproval, approver, approved, approvalDate,
                dateIssued, customerTerms);
    }
}
