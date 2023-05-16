package no.ding.pk.web.dto.web.client.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TermsDTO {
    private Long id;
    private Boolean additionForAdminFee;
    private Boolean additionForRoadTax;
    private Date agreementEndDate;
    private Date agreementStartDate;
    private String contractTerm;
    
    private String indexRent;
    private String indexTransport;
    private String indexWaste;
    private String invoiceInterval;
    private String metalPricing;
    private Date metalSetDateForOffer;
    private Integer monthsToFreezeCustomerFromAdjustments;
    private String newOrEstablishedCustomer;
    
    // Notification settings
    private Boolean customerRequireNotification;
    private String notificationMailAddress;
    private Integer requiredNumberOfDaysNotice;
    
    private String paymentCondition;
    private Date priceAdjustmentDate;
    private String quarterlyAdjustment;
    
    private String specialConditionAction;
    private String comment;

    private String salesOffice;
    private String customerName;
    private String customerNumber;
}
