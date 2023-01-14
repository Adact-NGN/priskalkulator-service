package no.ding.pk.web.enums;

public enum CustomerEstablishmentEnum {
    NEW_CUSTOMER("Ny kunde"),
    EXISTING_CUSTOMER("Eksisterende kunde");

    private String value;

    CustomerEstablishmentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
