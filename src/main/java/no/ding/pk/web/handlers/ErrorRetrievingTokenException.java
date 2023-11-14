package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Error retrieving token.")
public class ErrorRetrievingTokenException extends RuntimeException {
    public ErrorRetrievingTokenException() {
    }

    public ErrorRetrievingTokenException(String message) {
        super(message);
    }

    public ErrorRetrievingTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorRetrievingTokenException(Throwable cause) {
        super(cause);
    }

    public ErrorRetrievingTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
