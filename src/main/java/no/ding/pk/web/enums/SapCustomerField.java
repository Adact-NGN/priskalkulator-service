package no.ding.pk.web.enums;

public enum SapCustomerField {
    Kundenummer("Kundenummer"),
    Selskap("Selskap"),
    Navn1("Navn1"),
    KontaktPersoner("KontaktPersoner"),
    Kundetype("Kundetype");

    private String value;

    SapCustomerField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
