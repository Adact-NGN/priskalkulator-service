package no.ding.pk.utils;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestHeaderUtil {

    private final static Logger log = LoggerFactory.getLogger(RequestHeaderUtil.class);

    public static final String getBasicAuthenticationHeader(String username, String password) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            log.error("Authentication is not possible! Either username or password is not set.");
            throw new RuntimeException("Authentication is not possible! Either username or password is not set.");
        }
        String valueToEncode = username + ":" + password;
        
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
