package no.ding.pk.utils;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;

public class RequestHeaderUtil {

    @Value("${sap.username}")
    private String sapUsername;

    @Value("${sap.password}")
    private String sapPassword;

    public static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
