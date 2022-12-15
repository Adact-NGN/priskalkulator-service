package no.ding.pk.domain.offer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "terms")
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column
    private String contractTerm;

    @Column
    private Date agreementStartDate;

    @Column
    private Date agreementEndDate;

    @Column
    private String metalPricing;

    @Column
    private Date metalSetDateForOffer;

    @Column
    private String paymentCondition;

    @Column
    private String invoiceInterval;

    @Column
    private Boolean additionForRoadTax;

    @Column
    private Boolean additionForAdminFee;

    @Column
    private Boolean newOrEstablishedCustomer;

    // NG price terms addition
    @Column
    private String quarterlyAdjustment;

    @Column
    private Boolean customerRequireNotification; // false,

    @Column
    private String notificationMailAddress;

    @Column
    private Integer requiredNumberOfDaysNotice;

    @Column
    private Integer monthsToFreezeCustomerFromAdjustments; // 6,

    @Column
    private String indexWaste;

    @Column
    private String indexRent;

    @Column
    private String indexTransport;

    // Customer terms addition
    @Column
    private Date priceAdjustmentDate;
    
    @Column
    private String specialConditionAction;
}
