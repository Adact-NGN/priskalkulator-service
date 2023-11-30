package no.ding.pk.service.sap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import no.ding.pk.web.dto.sap.SalesOrgDTO;
import no.ding.pk.web.dto.v1.web.client.ZoneDTO;
import org.apache.commons.lang3.StringUtils;
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", "SalesOrganization eq '100'");
        params.add("$top", "100000");
        HttpRequest request = sapHttpClient.createGetRequest(salesOrgServiceUrl, params);
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

    @Cacheable(cacheNames = "sapSalesOfficeZoneCache", key = "#salesOffice")
    @Override
    public List<ZoneDTO> getZonesForSalesOffice(String salesOffice, String postalCode) {
        List<String> params = List.of("100", salesOffice, "");

        List<SalesOrgField> fieldList = List
                .of(SalesOrgField.SalesOrganization,
                        SalesOrgField.SalesOffice,
                        SalesOrgField.SalesZone);

        StringBuilder queryBuilder = new StringBuilder();

        String logicDivider = " and ";

        for(int i = 0; i < params.size(); i++) {
            String param = params.get(i);

            if(StringUtils.isNotBlank(param)) {
                String fieldType = fieldList.get(i).getType();

                if(Objects.equals(fieldList.get(i).getName(), SalesOrgField.City.getName())) {
                    param = param.toUpperCase();
                }

                log.debug("Parameter: {} fieldType: {}", param, fieldType);

                if(StringUtils.equals(fieldType, "numeric") && !StringUtils.isNumeric(param)) {
                    continue;
                }

                log.debug("Parameter ({}) and parameter type ({}) matches. Add to query.", param, fieldType);

                String field = fieldList.get(i).getName();
                addAndToQuery(queryBuilder, logicDivider);

                String comparator = " eq ";

                if(fieldList.get(i) == SalesOrgField.SalesZone) {
                    comparator = " ne ";
                }
                queryBuilder.append(field).append(comparator).append(String.format("'%s'", param));
            }
        }

        List<SalesOrgDTO> salesOrgDTOList = findByQuery(queryBuilder.toString(), 0);

        salesOrgDTOList.sort(Comparator.comparing(SalesOrgDTO::getSalesZone));

        SalesOrgDTO standardZone = getStandardZoneForPostalCode(postalCode, salesOrgDTOList);

        if(standardZone != null) {
            salesOrgDTOList.add(0, standardZone);
        }

        Map<String, SalesOrgDTO> distinctSalesOrgMap = new TreeMap<>();
        salesOrgDTOList.forEach(salesOrgDTO -> {
            if(StringUtils.isNotBlank(salesOrgDTO.getSalesZone()) && !distinctSalesOrgMap.containsKey(salesOrgDTO.getSalesZone())) {
                distinctSalesOrgMap.put(salesOrgDTO.getSalesZone(), salesOrgDTO);
            }
        });
        return distinctSalesOrgMap.values().stream().map(data -> {
            boolean isStandardZone = standardZone != null && StringUtils.isNotBlank(data.getSalesZone()) && StringUtils.equals(data.getSalesZone(), standardZone.getSalesZone());
            return ZoneDTO.builder()
                    .isStandardZone(isStandardZone)
                    .postalCode(data.getPostalCode())
                    .zoneId(data.getSalesZone().replace("0", ""))
                    .postalName(data.getCity()).build();
        }).collect(Collectors.toList());
    }

    private SalesOrgDTO getStandardZoneForPostalCode(String postalCode, List<SalesOrgDTO> salesOrgDTOList) {
        if(salesOrgDTOList.isEmpty()) {
            return null;
        }

        List<SalesOrgDTO> standardZones = salesOrgDTOList.stream().filter(salesOrgDTO -> salesOrgDTO.getPostalCode().equals(postalCode)).collect(Collectors.toList());

        if(standardZones.size() > 1) {
            standardZones = standardZones.stream().filter(salesOrgDTO -> StringUtils.isNotBlank(salesOrgDTO.getSalesZone())).collect(Collectors.toList());
        }

        if(!standardZones.isEmpty()) {
            return standardZones.get(0);
        }

        return null;
    }

    private void addAndToQuery(StringBuilder queryBuilder, String logicDivider) {
        if(!queryBuilder.isEmpty()) {
            queryBuilder.append(logicDivider);
        }
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
