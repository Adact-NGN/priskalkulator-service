package no.ding.pk.web.enums;

import java.util.List;

public enum SalesOrgField {
    SalesOrganization("SalesOrganization", "numeric"),
    SalesOffice("SalesOffice", "string"),
    PostalCode("PostalCode", "numeric"),
    SalesZone("SalesZone", "numeric"),
    City("City", "string"),
    SkipTokens("skiptokens", "numeric");
    
    private String value;
    private String type;
    
    SalesOrgField(String value, String type) {
        this.value = value;
        this.type = type;
    }
    
    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
    
    public static List<SalesOrgField> fieldList() {
        return List.of(SalesOrganization, 
        SalesOffice, 
        PostalCode, 
        SalesZone, 
        City);
    }
}
