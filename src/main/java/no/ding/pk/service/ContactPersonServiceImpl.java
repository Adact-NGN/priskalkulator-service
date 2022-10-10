package no.ding.pk.service;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.utils.HttpRequestResponseUtil;
import no.ding.pk.web.dto.ContactPersonDTO;

@Service
public class ContactPersonServiceImpl implements ContactPersonService {

    private static final Logger log = LoggerFactory.getLogger(ContactPersonServiceImpl.class);

    
    private String contactPersonSapServiceUrl;

    private String sapUsername;
    private String sapPassword;

    private ObjectMapper objectMapper;

    public ContactPersonServiceImpl(
        @Value(value = "${sap.username}") String sapUsername, 
        @Value(value = "${sap.password}") String sapPassword,
        @Value(value = "${sap.api.contact.person.url}") String contactPersonSapServiceUrl,
        ObjectMapper objectMapper) {
        this.sapUsername = sapUsername;
        this.sapPassword = sapPassword;
        this.contactPersonSapServiceUrl = contactPersonSapServiceUrl;

        this.objectMapper = objectMapper;
    }

    @Override
    public List<ContactPersonDTO> fetchContactPersons(List<String> expansionFields, Integer skipTokens) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if(!CollectionUtils.isEmpty(expansionFields)) {
            params.add("$expand", expansionFields.stream().collect(Collectors.joining(",")));
        } else {
            params = getDefaultParams();
        }

        if(skipTokens != null && skipTokens > 0) {
            params.add("$skiptoken", contactPersonSapServiceUrl);
        }

        HttpRequest request = HttpRequestResponseUtil.createGetRequest(contactPersonSapServiceUrl, params);
        
        HttpResponse<String> response = HttpRequestResponseUtil.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            log.debug("Request was successfull");
            List<ContactPersonDTO> customerDTOList = responseToContactPersonsDTOList(response);
            
            return customerDTOList;
        }

        log.debug(String.format("Requsting ContactPerson resulting in bad response: %d", response.statusCode()));

        return new ArrayList<>();
    }

    @Override
    public List<ContactPersonDTO> findContactPersonByNumber(String contactPersonNumber) {
        MultiValueMap<String, String> params = getDefaultParams();
        params.add("$filter", String.format("ContactPerson eq '%s'", contactPersonNumber));

        HttpRequest request = HttpRequestResponseUtil.createGetRequest(contactPersonSapServiceUrl, params);

        HttpResponse<String> response = HttpRequestResponseUtil.getResponse(request);

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
