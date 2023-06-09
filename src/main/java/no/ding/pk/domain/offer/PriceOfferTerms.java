package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.Auditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "price_offer_terms")
public class PriceOfferTerms extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String salesOrg;
    @Column
    private String salesOffice;
    @Column
    private String customerName;
    @Column
    private String customerNumber;
    @Column
    private String contractTerm;
    @Column
    private Boolean additionForAdminFee;
    @Column
    private Boolean additionForRoadTax;
    @Column
    private Date agreementEndDate;
    @Column
    private Date agreementStartDate;
    @Column
    private Boolean customerRequireNotification; // false,
    @Column
    private String indexRent;
    @Column
    private String indexTransport;
    @Column
    private String indexWaste;
    @Column
    private String invoiceInterval;
    @Column
    private String metalPricing;
    @Column
    private Date metalSetDateForOffer;
    @Column
    private Integer monthsToFreezeCustomerFromAdjustments; // 6,
    @Column
    private String newOrEstablishedCustomer;
    @Column
    private String notificationMailAddress;
    @Column
    private String paymentCondition;
    @Column
    private Date priceAdjustmentDate;
    @Column
    private String quarterlyAdjustment;
    @Column
    private Integer requiredNumberOfDaysNotice;
    @Column
    private String specialConditionAction;
    @Column
    private String comment;
}
