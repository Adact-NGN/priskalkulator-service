package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Wrong status was given")
public class WrongStatusException extends RuntimeException {
    public WrongStatusException() {
    }

    public WrongStatusException(String message) {
        super(message);
    }

    public WrongStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongStatusException(Throwable cause) {
        super(cause);
    }

    public WrongStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
