package no.ding.pk.web.enums;

import lombok.Getter;

@Getter
public enum ConditionPriceTypes {
    CURRENCY_DISCOUNT("Kronerabatt"),
    PCT_DISCOUNT("Prosentrabatt"),
    CUSTOMER_PRICE("Kundepris");

    private final String priceType;

    ConditionPriceTypes(String priceType) {
        this.priceType = priceType;
    }
}
