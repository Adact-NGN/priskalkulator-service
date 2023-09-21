package no.ding.pk.web.dto.v1.web.client.offer;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class CustomerTermsDTO {
    private Long id;
    private Integer number;
    private String level;
    private String nodeNr;
    private String salesOffice;
    private String customerName;
    private String customerNumber;
    private Boolean additionForAdminFee;
    private Boolean additionForRoadTax;
    private Date agreementEndDate;
    private Date agreementStartDate;
    private String contractTerm;
    private Boolean customerRequireNotification; // false,
    private String indexRent;
    private String indexTransport;
    private String indexWaste;
    private String invoiceInterval;
    private String metalPricing;
    private Date metalSetDateForOffer;
    private Integer monthsToFreezeCustomerFromAdjustments; // 6,
    private String newOrEstablishedCustomer;
    private String notificationMailAddress;
    private String paymentCondition;
    private Date priceAdjustmentDate;
    private String quarterlyAdjustment;
    private Integer requiredNumberOfDaysNotice;
    private String specialConditionAction;
    private String comment;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private String region;
    private Integer year;
    private String source;
    
}
