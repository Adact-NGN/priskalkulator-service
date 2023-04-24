package no.ding.pk.domain.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.Auditable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customer_terms")
public class CustomerTerms extends Auditable {
    @EmbeddedId
    private CustomerTermsPK id;

    @Column
    private String salesOffice;

    @Column
    private String customerName;

    @Column
    private String customerNumber;

    @Column
    private Boolean additionForAdminFee;
    @Column
    private Boolean additionForRoadTax;

    @Column
    private Date agreementEndDate;
    @Column
    private Date agreementStartDate;
    @Column
    private String contractTerm;

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

//    @Builder(builderMethodName = "customerTermsBuilder")
//    public CustomerTerms(String salesOffice, String customerName, String customerNumber, Boolean additionForAdminFee,
//                         Boolean additionForRoadTax, Date agreementEndDate, Date agreementStartDate, String contractTerm,
//                         Boolean customerRequireNotification, String indexRent, String indexTransport, String indexWaste,
//                         String invoiceInterval, String metalPricing, Date metalSetDateForOffer,
//                         Integer monthsToFreezeCustomerFromAdjustments, String newOrEstablishedCustomer,
//                         String notificationMailAddress, String paymentCondition, Date priceAdjustmentDate,
//                         String quarterlyAdjustment, Integer requiredNumberOfDaysNotice, String specialConditionAction,
//                         String comment, CustomerTermsPK id) {
//        super(salesOffice, customerName, customerNumber, additionForAdminFee, additionForRoadTax, agreementEndDate,
//                agreementStartDate, contractTerm, customerRequireNotification, indexRent, indexTransport, indexWaste,
//                invoiceInterval, metalPricing, metalSetDateForOffer, monthsToFreezeCustomerFromAdjustments,
//                newOrEstablishedCustomer, notificationMailAddress, paymentCondition, priceAdjustmentDate,
//                quarterlyAdjustment, requiredNumberOfDaysNotice, specialConditionAction, comment);
//        this.id = id;
//    }
}
