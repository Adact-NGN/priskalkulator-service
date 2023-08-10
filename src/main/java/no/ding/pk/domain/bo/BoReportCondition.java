package no.ding.pk.domain.bo;

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
