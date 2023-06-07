package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No sales approver provided")
public class ApproverNotFoundException extends RuntimeException {
    public ApproverNotFoundException() {
        super();
    }

    public ApproverNotFoundException(String message) {
        super(message);
    }

    public ApproverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApproverNotFoundException(Throwable cause) {
        super(cause);
    }
}
