package no.ding.pk.web.enums;

public enum PriceOfferStatus {
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    APPROVED("APPROVED"),
    SENT_TO_COSTUMER("SENT_TO_COSTUMER"),
    ACTIVATED("ACTIVATED");

    private final String status;

    PriceOfferStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
