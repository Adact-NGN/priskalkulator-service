package no.ding.pk.utils;

import javax.management.RuntimeErrorException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class LocalJSONUtils {

    private static final Logger log = LoggerFactory.getLogger(LocalJSONUtils.class);

    private static ObjectMapper objectMapper;
    
    @Autowired
    public LocalJSONUtils(ObjectMapper objectMapper) {
        LocalJSONUtils.objectMapper = objectMapper;
    }

    public static void checkForValidJSON(String userJson) {
        try {
            new JSONObject(userJson);
        } catch (JSONException e) {
            throw new RuntimeErrorException(new Error("JSON received from response is not valid, got: " + userJson));
        }
    }

    public static <T> T jsonStringToObject(String jsonString, T clazz) {
        log.debug("Getting JSON: " + jsonString);
        try {
            return (T) objectMapper.readValue(jsonString, clazz.getClass());
        } catch (JsonProcessingException e) {
            log.error("Exception thrown of type: ", e.getClass());
            log.error("Exception message: ", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not deserialize JSON to object."));
        }
    }
}
