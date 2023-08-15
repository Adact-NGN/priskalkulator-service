package no.ding.pk.domain.bo;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
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
    Boolean isWasteDisposalMaterial;
    Boolean isService;
    Boolean isRental;
    Boolean isProduct;
}
