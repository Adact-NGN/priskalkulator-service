package no.ding.pk.service.sap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ding.pk.web.dto.sap.SalesOrgDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.enums.SalesOrgField;

@Service
public class SalesOrgServiceImpl implements SalesOrgService {

    private static final Logger log = LoggerFactory.getLogger(SalesOrgServiceImpl.class);

    private final ObjectMapper objectMapper;

    private final String salesOrgServiceUrl;

    private final SapHttpClient sapHttpClient;

    public SalesOrgServiceImpl(
    @Value(value = "${sap.api.salesorg.url}") String salesOrgServiceUrl,
    @Autowired ObjectMapper objectMapper,
    SapHttpClient sapHttpClient) {
        this.salesOrgServiceUrl = salesOrgServiceUrl;

        this.objectMapper = objectMapper;
        this.sapHttpClient = sapHttpClient;
    }

    @Override
    @Cacheable("salesOrganizationCache")
    public List<SalesOrgDTO> getAll() {
        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, new LinkedMultiValueMap<>());
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }

        return new ArrayList<>();
    }

    @Override
    public List<SalesOrgDTO> findByQuery(String query, Integer skipTokens) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", query);
        params.add("$top", "5000");

        if(skipTokens != null && skipTokens > 0) {
            params.add("$skiptoken", skipTokens.toString());
        }

        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
        log.debug("Request uri: " + request.uri().toString());
        log.debug("Request query: " + request.uri().getQuery());
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }
        log.debug("Unsuccessful response: " + response.statusCode());
        return new ArrayList<>();
    }

    @Override
    public List<SalesOrgDTO> getAllBySalesOrganization(String salesOrg) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", String.format("%s eq '%s'", SalesOrgField.SalesOrganization.getName(), salesOrg));

        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }

        return new ArrayList<>();
    }

    @Override
    public List<SalesOrgDTO> getAllBySalesOffice(String salesOffice) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", String.format("%s eq '%s'", SalesOrgField.SalesOffice.getName(), salesOffice));

        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }

        return new ArrayList<>();
    }

    @Override
    public List<SalesOrgDTO> getAllByPostalNumber(String postalCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", String.format("%s eq '%s'", SalesOrgField.PostalCode.getName(), postalCode));

        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }

        return new ArrayList<>();
    }

    @Override
    public List<SalesOrgDTO> getAllBySalesZone(String salesZone) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", String.format("%s eq '%s'", SalesOrgField.SalesZone.getName(), salesZone));

        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }

        return new ArrayList<>();
    }

    @Override
    public List<SalesOrgDTO> getAllByCity(String city) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", String.format("%s eq '%s'", SalesOrgField.City.getName(), city));

        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            return responseToDtoList(response);
        }

        return new ArrayList<>();
    }

    private List<SalesOrgDTO> responseToDtoList(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        log.debug(String.format("JSON object size: %d", jsonObject.length()));
        JSONArray result = jsonObject.getJSONArray("value");

        log.debug(String.format("Got %d amount of objects in JSON array", result.length()));

        List<SalesOrgDTO> salesOrgDTOs = new ArrayList<>();

        Map<String, Integer> zoneCountMap = new HashMap<>();

        for(int i = 0; i < result.length(); i++) {
            SalesOrgDTO salesOrgDTO = jsonToSalesOrgDTO(result.get(i).toString());

            String salesOffice = salesOrgDTO.getSalesOffice();
            if(!zoneCountMap.containsKey(salesOffice)) {
                zoneCountMap.put(salesOffice, 1);
            } else {
                int currentAmount = zoneCountMap.get(salesOffice);
                zoneCountMap.put(salesOffice, currentAmount + 1);
            }

            salesOrgDTOs.add(salesOrgDTO);
        }

        return salesOrgDTOs;
    }

    private SalesOrgDTO jsonToSalesOrgDTO(String string) {
        try {
            return objectMapper.readValue(string, SalesOrgDTO.class);
        } catch(JsonProcessingException | JSONException e) {
            log.debug(e.getMessage());
            throw new Error("Failed to process JSON", e.getCause());
        }
    }
}
