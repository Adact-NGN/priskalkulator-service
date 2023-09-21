package no.ding.pk.web.enums;

import java.util.List;

public enum SalesOrgField {
    SalesOrganization("SalesOrganization", "numeric"),
    SalesOffice("SalesOffice", "numeric"),
    PostalCode("PostalCode", "numeric"),
    SalesOfficeName("SalesOfficeName", "string"),
    SalesZone("SalesZone", "numeric"),
    City("City", "string"),
    SkipTokens("skiptokens", "numeric");
    
    private final String name;
    private final String type;
    
    SalesOrgField(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
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
