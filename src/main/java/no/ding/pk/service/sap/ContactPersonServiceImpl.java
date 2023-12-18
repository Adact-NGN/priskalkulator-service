package no.ding.pk.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.ContactPersonDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@CacheConfig(cacheNames = "contactPersonCache")
@Service
public class ContactPersonServiceImpl implements ContactPersonService {

    private static final Logger log = LoggerFactory.getLogger(ContactPersonServiceImpl.class);

    
    private final String contactPersonSapServiceUrl;

    private final ObjectMapper objectMapper;

    private final SapHttpClient sapHttpClient;

    public ContactPersonServiceImpl(@Value(value = "${sap.api.contact.person.url}") String contactPersonSapServiceUrl,
                                    ObjectMapper objectMapper, SapHttpClient sapHttpClient) {
        this.contactPersonSapServiceUrl = contactPersonSapServiceUrl;

        this.objectMapper = objectMapper;
        this.sapHttpClient = sapHttpClient;
    }

    @Override
    public List<ContactPersonDTO> fetchContactPersons(List<String> expansionFields, Integer skipTokens) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if(!CollectionUtils.isEmpty(expansionFields)) {
            params.add("$expand", String.join(",", expansionFields));
        } else {
            params = getDefaultParams();
        }

        if(skipTokens != null && skipTokens > 0) {
            params.add("$skiptoken", contactPersonSapServiceUrl);
        }

        HttpRequest request = sapHttpClient.createGetRequest(contactPersonSapServiceUrl, params);
        
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            log.debug("Request was successful");

            return responseToContactPersonsDTOList(response);
        }

        log.debug(String.format("Requsting ContactPerson resulting in bad response: %d", response.statusCode()));

        return new ArrayList<>();
    }

    @Cacheable(key = "#contactPersonNumber")
    @Override
    public List<ContactPersonDTO> findContactPersonByNumber(String contactPersonNumber) {
        MultiValueMap<String, String> params = getDefaultParams();
        params.add("$filter", String.format("ContactPerson eq '%s'", contactPersonNumber));

        HttpRequest request = sapHttpClient.createGetRequest(contactPersonSapServiceUrl, params);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToContactPersonsDTOList(response);
        }
        return new ArrayList<>();
    }

    private List<ContactPersonDTO> responseToContactPersonsDTOList(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray results = jsonObject.getJSONArray("value");
        
        List<ContactPersonDTO> customerDTOs = new ArrayList<>();
        
        for(int i = 0; i < results.length(); i++) {
            ContactPersonDTO customerDTO = jsonToContactPersonDTO(results.get(i).toString());
            customerDTOs.add(customerDTO);
        }
        return customerDTOs;
    }

    private ContactPersonDTO jsonToContactPersonDTO(String contactPersonJson) {
        try {
            return objectMapper.readValue(contactPersonJson, ContactPersonDTO.class);
        } catch (JsonProcessingException | JSONException e) {
            log.debug("Received JSON string: \n" + contactPersonJson);
            throw new Error("Failed to process JSON", e.getCause());
        }
    }

    private MultiValueMap<String, String> getDefaultParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$expand", "_Customers");

        return params;
    }
}
