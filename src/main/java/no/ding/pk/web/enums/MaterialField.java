package no.ding.pk.web.enums;

public enum MaterialField {
    ValidFrom("ValidFrom"), //: /Date(1668556800000)/,
    SalesOrganization("SalesOrganization"), //: 100,
    SalesOffice("SalesOffice"), //: 104,
    Material("Material"), //: 50000,
    MaterialDescription("MaterialDescription"), //: Mal for tjenester,
    DeviceCategory("DeviceCategory"),
    DistributionChannel("DistributionChannel"),
    SalesZone("SalesZone"),
    ScaleQuantity("ScaleQuantity"), //: 0.000,
    StandardPrice("StandardPrice"), //: 0.00,
    Valuta("Valuta"), //: ,
    PricingUnit("PricingUnit"), //: 1000,
    QuantumUnit("QuantumUnit"), //: KG,
    MaterialExpired("MaterialExpired"),
    ValidTo("ValidTo"),
    MaterialGroup("MaterialGroup"),
    MaterialGroupDescription("MaterialGroupDescription"),
    MaterialType("MaterialType"),
    MaterialTypeDescription("MaterialTypeDescription"),

    // Fields specific for SAP Material API.
    CategoryId("CategoryId"),
    CategoryDescription("CategoryDescription"),
    SubCategoryId("SubCategoryId"),
    SubCategoryDescription("SubCategoryDescription"),
    ClassId("ClassId"),
    ClassDescription("ClassDescription");

    private final String value;

    MaterialField(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
