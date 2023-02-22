package no.ding.pk.web.enums;

public enum LogicOperator {
    And("and"),
    Or("or");

    private final String value;

    LogicOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
