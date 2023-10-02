package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Required property is missing.")
public class MissingRequiredPropertyException extends RuntimeException {
    public MissingRequiredPropertyException() {
    }

    public MissingRequiredPropertyException(String message) {
        super(message);
    }

    public MissingRequiredPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingRequiredPropertyException(Throwable cause) {
        super(cause);
    }

    public MissingRequiredPropertyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
