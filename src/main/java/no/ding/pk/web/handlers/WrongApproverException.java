package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No sales office provided")
public class WrongApproverException extends RuntimeException {
    public WrongApproverException() {
        super();
    }

    public WrongApproverException(String message) {
        super(message);
    }

    public WrongApproverException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongApproverException(Throwable cause) {
        super(cause);
    }

    public WrongApproverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
