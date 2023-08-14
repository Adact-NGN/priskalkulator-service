package no.ding.pk.domain.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoReportCondition {
    String terms;
    Boolean hasSalesOrg;
    Boolean isPricedOnSalesOffice;
    Boolean isCustomer;
    Boolean isNode; // Technically, this is isCustomer == false
    Boolean isZoneMaterial;
    Boolean isWaste;
    Boolean hasDevicePlacement;
    Boolean isDeviceType;
    Boolean hasSalesDocument;
}
