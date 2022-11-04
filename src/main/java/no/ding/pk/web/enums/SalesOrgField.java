package no.ding.pk.web.enums;

import java.util.List;

public enum SalesOrgField {
    SalesOrganization("SalesOrganization"),
    SalesOffice("SalesOffice"),
    PostalNumber("PostalNumber"),
    SalesZone("SalesZone"),
    City("City"),
    SkipTokens("skiptokens");
    
    private String value;
    
    SalesOrgField(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static List<String> fieldList() {
        return List.of(SalesOrganization.value, 
        SalesOffice.value, 
        PostalNumber.value, 
        SalesZone.value, 
        City.value);
    }
}
