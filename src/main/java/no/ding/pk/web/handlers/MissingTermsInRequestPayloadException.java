package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Missing terms object in request.")
public class MissingTermsInRequestPayloadException extends RuntimeException {
    public MissingTermsInRequestPayloadException() {
    }

    public MissingTermsInRequestPayloadException(String message) {
        super(message);
    }

    public MissingTermsInRequestPayloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingTermsInRequestPayloadException(Throwable cause) {
        super(cause);
    }

    public MissingTermsInRequestPayloadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
