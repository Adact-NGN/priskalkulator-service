package no.ding.pk.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidator implements OAuth2TokenValidator<Jwt> {

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (token.getAudience().contains("<our-azure-ad-application-id>")) {
            return OAuth2TokenValidatorResult.success();
        } else {
            // ErrorCode class below is our custom enum class which we have created.
            return OAuth2TokenValidatorResult.failure(new OAuth2Error(ErrorCode.AUTHENTICATION_FAILURE.getErrorCode()));
        }
    }
}
