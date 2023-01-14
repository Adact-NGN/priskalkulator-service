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
}
