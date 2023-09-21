package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unknown Price Offer status given.")
public class MissingApprovalStatusException extends RuntimeException {
    public MissingApprovalStatusException() {
    }

    public MissingApprovalStatusException(String message) {
        super(message);
    }

    public MissingApprovalStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingApprovalStatusException(Throwable cause) {
        super(cause);
    }
}
