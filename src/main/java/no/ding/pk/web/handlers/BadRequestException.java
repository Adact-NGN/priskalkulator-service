package no.ding.pk.web.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Malformed request")
public class BadRequestException extends RuntimeException {
    public BadRequestException(String s) {
        super(s);
    }
}
