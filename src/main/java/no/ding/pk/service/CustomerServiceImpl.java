package no.ding.pk.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.utils.RequestHeaderUtil;
import no.ding.pk.web.dto.CustomerDTO;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    private ObjectMapper objectMapper;

    private String customerSapServiceUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_hp_customers2/srvd_a2x/sap/zapi_hp_customers/0001/Kunder";
    
    private String sapUsername;
    private String sapPassword;
    
    @Autowired
    public CustomerServiceImpl(
    @Value(value = "${sap.username}") String username,
    @Value(value = "${sap.password}") String password,
    @Value(value = "${sap.api.customer.url}") String customerSapServiceUrl,
    ObjectMapper objectMapper) {
        this.sapUsername = username;
        this.sapPassword = password;
        this.objectMapper = objectMapper;
    }
    
    public List<CustomerDTO> fetchCustomersJSON(String motherCompany, String customerType, List<String> expansionFields, Integer skipToken) {
        String localMotherCompany = checkString(motherCompany, "");

        StringBuilder filterString = new StringBuilder();

        filterString.append(String.format("Morselskap eq '%s", localMotherCompany));

        if(!StringUtils.isBlank(customerType)) {
            filterString.append(" and ");
            filterString.append(String.format("Kundetype eq '%s'", customerType));
        }
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterString.toString());
        params.add("$format", "json");
        
        if(!CollectionUtils.isEmpty(expansionFields)) {
            if(!expansionFields.contains("KontaktPersoner")) {
                expansionFields.add("KontaktPersoner");
            }

            params.add("$expand", expansionFields.stream().collect(Collectors.joining(",")));
        } else {
            params = getDefaultParams();
        }

        if(skipToken != null) {
            params.add("$skiptoken", skipToken.toString());
        }
        
        HttpRequest request = createGetRequest(params);
        
        HttpResponse<String> response = getResponse(request);
        
        if(response.statusCode() == HttpStatus.OK.value()) {
            List<CustomerDTO> customerDTOList = responseToCustomerDTOList(response);
            
            return customerDTOList;
        }
        
        return new ArrayList<>();
    }
    
    public List<CustomerDTO> findCustomersBySalesOrgAndName(String salesOrg, String name) {
        MultiValueMap<String, String> params = getDefaultParams();

        params.add("$filter", String.format("Selskap eq '%s' and Navn1 eq ''", salesOrg, name));

        HttpRequest request = createGetRequest(params);
        HttpResponse<String> response = getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToCustomerDTOList(response);
        }

        return new ArrayList<CustomerDTO>();
    }

    public List<CustomerDTO> findCustomerByCustomerNumber(String knr) {
        MultiValueMap<String, String> params = getDefaultParams();
        params.add("$filter", String.format("Kundenummer eq '%s'", knr));

        HttpRequest request = createGetRequest(params);

        HttpResponse<String> response = getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToCustomerDTOList(response);
        }
        return new ArrayList<CustomerDTO>();
    }

    @Override
    public List<CustomerDTO> findCustomersBySalesOrg(String salesOrg) {
        MultiValueMap<String, String> params = getDefaultParams();
        params.add("$filter", String.format("Selskap eq '%s'", salesOrg));

        HttpRequest request = createGetRequest(params);

        HttpResponse<String> response = getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToCustomerDTOList(response);
        }

        return new ArrayList<CustomerDTO>();
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        HttpClient client = HttpClient.newBuilder()
        .build();

        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Error(e.getMessage());
        }
    }

    private String checkString(String input, String defaultValue) {
        if(StringUtils.isBlank(input)) {
            return defaultValue;
        }
        
        return input;
    }
    
    private List<CustomerDTO> responseToCustomerDTOList(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray results = jsonObject.getJSONArray("value");
        
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        
        for(int i = 0; i < results.length(); i++) {
            CustomerDTO customerDTO = jsonToCustomerDTO(results.get(i).toString());
            customerDTOs.add(customerDTO);
        }
        return customerDTOs;
    }

    private CustomerDTO jsonToCustomerDTO(String customerDTOString) {
        try {
            return objectMapper.readValue(customerDTOString, CustomerDTO.class);
        } catch (JsonProcessingException | JSONException e) {
            throw new Error("Failed to process JSON", e.getCause());
        }
    }

    private HttpRequest createGetRequest(MultiValueMap<String, String> params) {
        UriComponents url = UriComponentsBuilder
        .fromUriString(customerSapServiceUrl)
        .queryParams(params)
        .build();

        return HttpRequest.newBuilder()
        .GET()
        .uri(url.toUri())
        .header(HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword))
        .build();
    }

    private MultiValueMap<String, String> getDefaultParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$expand", "KontaktPersoner");

        return params;
    }

    
}
