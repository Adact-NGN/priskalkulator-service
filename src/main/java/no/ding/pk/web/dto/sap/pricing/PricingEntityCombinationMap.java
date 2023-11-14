package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder(builderMethodName = "hiddenBuilder")
@Data
public class PricingEntityCombinationMap {
    private String salesOrg;
    private String salesOffice;
    private String materialNumber;
    private Integer zone;
    private Date from;
    private Date to;
    private String conditionCode;
    private String keyCombinationTableName;
    private Double rateValue;
    private String valueUnit;

    @JsonIgnore
    public String getMaterialId() {
        StringBuilder sb = new StringBuilder();

        sb.append(salesOrg).append("_").append(salesOffice).append("_").append(materialNumber);

        if(zone != null) {
            if(zone > 9) {
                sb.append("_").append(String.format("%d", zone));
            } else {
                sb.append("_").append(String.format("0%d", zone));
            }
        }

        return sb.toString();
    }

    public static PricingEntityCombinationMapBuilder builder(String salesOrg, String salesOffice, String materialNumber,
                                                             String conditionCode, String keyCombinationTableName,
                                                             Double rateValue, String valueUnit) {
        return hiddenBuilder().salesOrg(salesOrg).salesOffice(salesOffice).materialNumber(materialNumber)
                .conditionCode(conditionCode).keyCombinationTableName(keyCombinationTableName)
                .rateValue(rateValue).valueUnit(valueUnit);
    }
}
