package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

// Price Condition Record Validity

/**
 * Condition Record Validity (A_SlsPrcgCndnRecdValidity)
 * Validity of condition record
 * This entity corresponds to the validity of condition records for pricing that are used in Sales.
 * The validity of a condition record is characterized by a validity start date and end date, business attributes
 * (such as business partner, material, and so on), and a condition type.
 * The start and end date will be handled automatically in sap if not set.
 */
@Builder(builderMethodName = "hiddenBuilder")
@Data
@JsonRootName("results")
public class ConditionRecordValidityDTO {
    @JsonProperty(value = "ConditionRecord")
    private String conditionRecord;

    @JsonProperty(value = "SalesOrganization", required = true)
    private String salesOrganization;

    @JsonProperty(value = "SalesOffice", required = true)
    private String salesOffice;

    @JsonProperty(value = "Customer", required = true)
    private String customer;

    @JsonProperty(value = "Material", required = true)
    private String material;

    /**
     * Equivalent to zone. <br/>
     * Format example '01'
     */
    @JsonProperty(value = "CustomerConditionGroup")
    private String customerConditionGroup;

    public static ConditionRecordValidityDTOBuilder builder(String salesOrganization, String salesOffice, String customer, String material) {
        return hiddenBuilder().salesOrganization(salesOrganization).salesOffice(salesOffice).customer(customer).material(material);
    }
}
