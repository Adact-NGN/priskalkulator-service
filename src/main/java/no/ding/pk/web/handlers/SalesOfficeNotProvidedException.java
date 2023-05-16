package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No sales office provided")
public class SalesOfficeNotProvidedException extends RuntimeException{
     public SalesOfficeNotProvidedException() {
        super();
     }

     public SalesOfficeNotProvidedException(String message) {
        super(message);
     }

     public SalesOfficeNotProvidedException(String message, Throwable cause) {
        super(message, cause);
     }

     public SalesOfficeNotProvidedException(Throwable cause) {
        super(cause);
     }

     public SalesOfficeNotProvidedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
     }
}
