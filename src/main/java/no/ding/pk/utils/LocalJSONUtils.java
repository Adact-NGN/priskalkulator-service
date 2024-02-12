package no.ding.pk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;

@Component
public class LocalJSONUtils {

    private static final Logger log = LoggerFactory.getLogger(LocalJSONUtils.class);

    private ObjectMapper objectMapper;

    @Autowired
    public LocalJSONUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void checkForValidJSON(String userJson) {
        try {
            new JSONObject(userJson);
        } catch (JSONException e) {
            throw new RuntimeErrorException(new Error("JSON received from response is not valid, got: " + userJson));
        }
    }

    public <T> T jsonStringToObject(String jsonString, Class<T> clazz) {
        try {
            return (T) objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("Exception thrown of type: {}", e.getClass());
            log.error("Exception message: {}", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not deserialize JSON to object."));
        }
    }

    public <T> List<T> jsonToObjects(String json, Class<T> clazz) {
        JSONObject jsonObject = new JSONObject(json);

        if(jsonObject.has("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");

            log.debug("code: " + errorObject.getString("code"));
            log.debug("message" + errorObject.getJSONObject("message").getString("value"));
        }


        JSONArray results = null;
        if(jsonObject.has("d")) {
            results = jsonObject.getJSONObject("d").getJSONArray("results");
        } else if(jsonObject.has("value")) {
            results = jsonObject.getJSONArray("value");
        }

        List<T> objectList = new ArrayList<>();
        if (results != null) {
            log.debug(String.format("JSON array contains %d elements", results.length()));

            for(int i = 0; i < results.length(); i++) {
                JSONObject o = (JSONObject) results.get(i);

                T object = jsonStringToObject(o.toString(), clazz);
                objectList.add(object);
            }
        }

        return objectList;
    }
}
