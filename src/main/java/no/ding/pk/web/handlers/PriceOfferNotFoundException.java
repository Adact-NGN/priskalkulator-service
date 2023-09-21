package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Could not find price offer")
public class PriceOfferNotFoundException extends RuntimeException {
    public PriceOfferNotFoundException() {
        super();
    }

    public PriceOfferNotFoundException(String message) {
        super(message);
    }

    public PriceOfferNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PriceOfferNotFoundException(Throwable cause) {
        super(cause);
    }
}
