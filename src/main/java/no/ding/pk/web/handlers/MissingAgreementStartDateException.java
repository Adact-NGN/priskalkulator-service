package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No agreement start date was provided")
public class MissingAgreementStartDateException extends RuntimeException {
    public MissingAgreementStartDateException() {
        super();
    }

    public MissingAgreementStartDateException(String message) {
        super(message);
    }

    public MissingAgreementStartDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingAgreementStartDateException(Throwable cause) {
        super(cause);
    }

    public MissingAgreementStartDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
