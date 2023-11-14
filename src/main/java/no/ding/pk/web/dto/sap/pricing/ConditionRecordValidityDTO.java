package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
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
@Builder
@Data
public class ConditionRecordValidityDTO {
    @JsonProperty(value = "results")
    private List<ConditionRecordValidityItemDTO> conditionRecordValidityItemDTOS;

    public void addConditionRecordValidity(ConditionRecordValidityItemDTO validityDTO) {
        if(conditionRecordValidityItemDTOS == null) {
            conditionRecordValidityItemDTOS = new ArrayList<>();
        }

        conditionRecordValidityItemDTOS.add(validityDTO);
    }
}
