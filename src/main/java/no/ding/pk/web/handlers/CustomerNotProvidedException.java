package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No sales employee provided")
public class CustomerNotProvidedException extends RuntimeException {
    public CustomerNotProvidedException() {
        super();
    }

    public CustomerNotProvidedException(String message) {
        super(message);
    }

    public CustomerNotProvidedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerNotProvidedException(Throwable cause) {
        super(cause);
    }

    protected CustomerNotProvidedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
