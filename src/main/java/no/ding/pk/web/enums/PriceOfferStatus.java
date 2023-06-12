package no.ding.pk.web.enums;

import java.util.List;

public enum PriceOfferStatus {
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    APPROVED("APPROVED"),
    SENT_TO_COSTUMER("SENT_TO_COSTUMER"),
    ACTIVATED("ACTIVATED"),
    SENT_TO_SAP("SENT_TO_SAP");

    private final String status;

    PriceOfferStatus(String status) {
        this.status = status;
    }

    public static List<String> getAllViewableStates() {
        return List.of(PENDING.status, REJECTED.status, APPROVED.status, SENT_TO_COSTUMER.status);
    }

    public static List<String> getApprovalStates() {
        return List.of(APPROVED.status, SENT_TO_COSTUMER.status);
    }

    public static List<String> getAllPriceOfferStatuses() {
        return List.of(PENDING.status, REJECTED.status, APPROVED.status, SENT_TO_COSTUMER.status, ACTIVATED.status);
    }

    public static boolean isApprovalState(String status) {
        return getApprovalStates().contains(status);
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
