package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A_SlsPrcgCndnRecdSuplmnt
 */
@Data
public class ConditionSupplementsDTO {
    @JsonProperty("ConditionRecord")
    private String conditionRecord;
    @JsonProperty("ConditionSequentialNumber")
    private String conditionSequentialNumber;
    @JsonProperty("ConditionScaleLine")
    private String conditionScaleLine;

    @JsonProperty("ConditionScaleQuantity")
    private String conditionScaleQuantity;

    @JsonProperty("ConditionScaleQuantityUnit")
    private String conditionScaleQuantityUnit;
    @JsonProperty("ConditionScaleAmount")
    private ScaleValueDTO conditionScaleAmount;
    @JsonProperty("ConditionScaleAmountCurrency")
    private String conditionScaleAmountCurrency;
    @JsonProperty("ConditionRateValue")
    private String conditionRateValue;
    @JsonProperty("ConditionRateValueUnit")
    private String conditionRateValueUnit;
    @JsonProperty("ETag")
    private String eTag;
    @JsonProperty("to_SlsPrcgCndnRecdSuplmnt")
    private AdditionalConditionsDTO to_SlsPrcgCndnRecdSuplmnt;
    @JsonProperty("to_SlsPrcgConditionRecord")
    private ConditionRecordDTO to_SlsPrcgConditionRecord;
}
