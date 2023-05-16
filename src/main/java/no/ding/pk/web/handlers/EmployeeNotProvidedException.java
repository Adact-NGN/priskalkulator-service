package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No sales employee provided")
public class EmployeeNotProvidedException extends RuntimeException {
    
    public EmployeeNotProvidedException() {
        super();
    }
    
    public EmployeeNotProvidedException(String message) {
        super(message);
    }
    
    public EmployeeNotProvidedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EmployeeNotProvidedException(Throwable cause) {
        super(cause);
    }
    
    public EmployeeNotProvidedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
