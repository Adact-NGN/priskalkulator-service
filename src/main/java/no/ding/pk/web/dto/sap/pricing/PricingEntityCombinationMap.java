package no.ding.pk.web.dto.sap.pricing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

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
}
