package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PriceOfferStatusCodeNotFoundException extends RuntimeException {
    public PriceOfferStatusCodeNotFoundException() {
    }

    public PriceOfferStatusCodeNotFoundException(String message) {
        super(message);
    }

    public PriceOfferStatusCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PriceOfferStatusCodeNotFoundException(Throwable cause) {
        super(cause);
    }

    public PriceOfferStatusCodeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
