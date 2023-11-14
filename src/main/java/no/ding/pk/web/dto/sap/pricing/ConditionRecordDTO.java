package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to use as request body to /A_SlsPrcgConditionRecord for setting material prices in SAP
 * For more detials:
 * <a href="https://sapdev.norskgjenvinning.no/sap(bD1ubyZjPTEwMA==)/bc/bsp/sap/zgw_openapi/index.html?service=API_SLSPRICINGCONDITIONRECORD_SRV&version=0001&repository=&group=#/Prisskalaer/post_A_SlsPrcgConditionRecord___ConditionRecord____to_SlsPrcgCndnRecordScale">...</a>
 */
@Builder(builderMethodName = "hiddenBuilder")
@Data
public class ConditionRecordDTO {
    @JsonProperty(value = "ConditionTable")
    private String conditionTable;
    @JsonProperty(value = "ConditionType")
    private String conditionType; //"ZR05",

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty(value = "ConditionRateValue")
    private String conditionRateValue; //"-1749.00", // beløp som skal inn 0.00 NOK/% maks to desimaler
    @JsonProperty(value = "ConditionRateValueUnit")
    private String conditionRateValueUnit; //"NOK",
    @JsonProperty(value = "ConditionQuantity")
    private Double conditionQuantity; //"1000",
    @JsonProperty(value = "ConditionQuantityUnit")
    private String conditionQuantityUnit; // "KG"

    @JsonProperty(value = "to_SlsPrcgCndnRecdValidity")
    private ConditionRecordValidityDTO conditionRecordValidity;

    public void setConditionRateValue(Double value) {
        this.conditionRateValue = String.format("%.2f", value);
    }

    public static ConditionRecordDTOBuilder builder(String conditionTable,
                                                    String conditionType,
                                                    Double conditionRateValue,
                                                    String conditionRateValueUnit,
                                                    Double conditionQuantity,
                                                    String conditionQuantityUnit) {
        return hiddenBuilder().conditionTable(conditionTable)
                .conditionType(conditionType)
                .conditionRateValue(String.format("%.2f", conditionRateValue))
                .conditionRateValueUnit(conditionRateValueUnit)
                .conditionQuantity(conditionQuantity)
                .conditionQuantityUnit(conditionQuantityUnit);
    }
}
