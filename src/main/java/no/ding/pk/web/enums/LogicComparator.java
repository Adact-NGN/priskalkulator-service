package no.ding.pk.web.enums;

public enum LogicComparator {
    Equal("eq"),
    NotEqual("ne");

    private final String value;

    LogicComparator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
