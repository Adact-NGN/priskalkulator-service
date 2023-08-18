package no.ding.pk.web.enums;

public enum CustomerType {
    NODE("Node"),
    ORGANIZATION("Organisasjon");
    private String type;

    CustomerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
