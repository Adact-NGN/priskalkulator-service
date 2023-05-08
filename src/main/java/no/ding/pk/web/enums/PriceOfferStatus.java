package no.ding.pk.web.enums;

public enum PriceOfferStatus {
    OFFER_CREATED("Tilbud opprettet"),
    SENT_TO_COSTUMER("Sendt til kunde"),
    FOR_APPROVAL("Til godkjenning"),
    APPROVED_SENT_TO_CUSTOMER("Godkjent, sendt til kunde"),
    ACTIVATED("Aktivert");

    private String status;

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
