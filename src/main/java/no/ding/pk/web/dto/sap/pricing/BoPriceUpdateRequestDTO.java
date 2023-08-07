package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BoPriceUpdateRequestDTO {
    @JsonProperty("ConditionalTable")
    private String conditionalTable;
    @JsonProperty("ConditionalApplication")
    private final String conditionalApplication = "V";
    @JsonProperty("ConditionalType")
    private String conditionalType; // Which sap table to add the price
    @JsonProperty("ConditionalRateValue")
    private String conditionalRateValue; // Discount
    @JsonProperty("ConditionalRateValueUnit")
    private final String conditionalRateValueUnit = "NOK"; // Material enhet
    @JsonProperty("ConditionQuantity")
    private String conditionQuantity; // Kvantitet fra materialobjektet
    @JsonProperty("ConditionQuantityUnit")
    private String conditionQuantityUnit; // Enhet fra materialobjektet
    @JsonProperty("to_SlsPrcgCndnRecdValidity")
    private List<ConditionRecordValidityDTO> to_SlsPrcgCndnRecdValidity;
}
