package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find price offer template")
public class PriceOfferTemplateNotFound extends RuntimeException {
    public PriceOfferTemplateNotFound() {
    }

    public PriceOfferTemplateNotFound(String message) {
        super(message);
    }

    public PriceOfferTemplateNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public PriceOfferTemplateNotFound(Throwable cause) {
        super(cause);
    }

    public PriceOfferTemplateNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
