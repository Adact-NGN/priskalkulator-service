package no.ding.pk.web.enums;

import java.util.List;

public enum TermsTypes {
    GeneralTerms("Generelle vilkår"),
    NGPriceTerms("NG Prisvilkår"),
    CustomerTerms("Kundens vilkår");

    private final String value;

    TermsTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> fieldList() {
        return List.of(GeneralTerms.value,
                NGPriceTerms.value,
                CustomerTerms.value);
    }
}
