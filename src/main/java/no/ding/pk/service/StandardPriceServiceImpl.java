package no.ding.pk.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.utils.RequestHeaderUtil;
import no.ding.pk.web.enums.MaterialField;

@Service
public class StandardPriceServiceImpl implements StandardPriceService {
    
    private static final Logger log = LoggerFactory.getLogger(StandardPriceServiceImpl.class);
    
    @Value("${sap.api.standard.price.url}")
    private final String standardPriceSapUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet";
    
    private String sapUsername;
    private String sapPassword;
    
    private ObjectMapper objectMapper;
    
    private InMemoryCache<String, String, MaterialStdPriceDTO> inMemoryCache;
    
    @Autowired
    public StandardPriceServiceImpl(
    @Value(value = "${sap.username}") String sapUsername, 
    @Value("${sap.password}") String sapPassword,
    ObjectMapper objectMapper,
    InMemoryCache<String, String, MaterialStdPriceDTO> inMemoryCache
    ) {
        this.sapUsername = sapUsername;
        this.sapPassword = sapPassword;
        this.objectMapper = objectMapper;
        this.inMemoryCache = inMemoryCache;
    }
    
    @Override
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg) {
        if(inMemoryCache.size(salesOffice) == 0 || inMemoryCache.isExpired()) {
            log.debug("Cache is empty or expired, fetching new items.");
            
            String filterQuery = createFilterQuery(salesOffice, salesOrg);
            
            log.debug(String.format("Filter query: %s", filterQuery));
            
            buildUpStandardPriceCache(salesOffice, filterQuery);
            
            log.debug("Returning from new cache");
        }
        
        return inMemoryCache.getAll(salesOffice);        
    }
    
    
    
    @Override
    public MaterialPrice getStandardPriceForMaterial(String materialNumber, String salesOrg, String salesOffice) {
        
        if(inMemoryCache.size(salesOffice) == 0 || inMemoryCache.isExpired()) {
            log.debug("Cache is empty or expired, fetching new items.");
            
            String filterQuery = createFilterQuery(salesOffice, salesOrg, materialNumber);
            
            log.debug(String.format("Filter query: %s", filterQuery));
            
            buildUpStandardPriceCache(salesOffice, filterQuery);
            
            log.debug("Returning from new cache");
        }
        
        MaterialStdPriceDTO materialStdPriceDTO = inMemoryCache.getAll(salesOffice).stream().filter(material -> materialNumber.equals(material.getMaterial())).findAny().orElse(null);
        
        if(materialStdPriceDTO != null) {
            return materialDtoToMaterialPrice(materialNumber, materialStdPriceDTO);
        }
        
        return null;
    }
    
    private MaterialPrice materialDtoToMaterialPrice(String materialNumber, MaterialStdPriceDTO materialStdPriceDTO) {
        return MaterialPrice.builder()
        .materialNumber(materialNumber)
        .standardPrice(Double.parseDouble(materialStdPriceDTO.getStandardPrice()))
        .validFrom(materialStdPriceDTO.getValidFrom())
        .validTo(materialStdPriceDTO.getValidTo())
        .build();
    }
    
    private void buildUpStandardPriceCache(String salesOffice, String filterQuery) {
        inMemoryCache.cleanUp();
        
        HttpRequest request = createRequest(filterQuery);
        
        log.debug("Created request: " + request.toString());
        
        HttpResponse<String> response = sendRequest(request);
        
        List<MaterialStdPriceDTO> standardPriceDTOList = jsonToMaterialDTO(response);
        
        addMaterialsToCache(salesOffice, standardPriceDTOList);
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg) {
        return createFilterQuery(salesOffice, salesOrg, null);
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg, String materialNumber) {
        StringBuilder filterQuery = new StringBuilder();
        filterQuery.append(
        String.format("%s eq '%s' and %s eq '%s' and %s eq ''", 
        MaterialField.SalesOffice.getValue(), salesOffice, 
        MaterialField.SalesOrganization.getValue(), salesOrg, 
        MaterialField.MaterialExpired.getValue()
        ));
        
        if(StringUtils.isNotBlank(materialNumber)) {
            filterQuery.append(String.format(" and %s eq '%s'", MaterialField.Material.getValue(), materialNumber));
        }
        return filterQuery.toString();
    }
    
    private HttpRequest createRequest(String filterQuery) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");
        
        UriComponents url = UriComponentsBuilder
        .fromUriString(standardPriceSapUrl)
        .queryParams(params)
        .build();
        
        log.debug("Created URL with URL prams: " + url.toUri().toString());
        
        return HttpRequest.newBuilder()
        .GET()
        .uri(url.toUri())
        .header(HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword))
        .build();
    }
    
    private HttpResponse<String> sendRequest(HttpRequest request) {
        HttpClient client = HttpClient.newBuilder()
        .build();
        
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Error(e.getMessage(), e);
        }
    }
    
    private void addMaterialsToCache(String salesOffice, List<MaterialStdPriceDTO> standardPriceDTOList) {
        log.debug(String.format("Adding %d items to cache.", standardPriceDTOList.size()));
        for(MaterialStdPriceDTO material : standardPriceDTOList) {
            StringBuffer objectKey = new StringBuffer();
            objectKey.append(material.getMaterial());
            
            if(!StringUtils.isBlank(material.getDeviceType())) {
                objectKey.append("_").append(material.getDeviceType());
            }
            inMemoryCache.put(salesOffice, objectKey.toString(), material);
        }
        int amountAddedForSalesOffice = inMemoryCache.size(salesOffice);
        log.debug(String.format("Added %d items to cache.", amountAddedForSalesOffice));
    }
    
    private List<MaterialStdPriceDTO> jsonToMaterialDTO(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        
        if(jsonObject.has("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            
            log.debug("code: " + errorObject.getString("code"));
            log.debug("message" + errorObject.getJSONObject("message").getString("value"));
        }
        
        JSONArray results = jsonObject.getJSONObject("d").getJSONArray("results");
        log.debug(String.format("JSON array contains %d elements", results.length()));
        List<MaterialStdPriceDTO> standardPriceDTOList = new ArrayList<>();
        
        int amountOfSuccessfullMaps = 0;
        int amountOfUnSuccessfullMaps = 0;
        for(int i = 0; i < results.length(); i++) {
            try {
                MaterialStdPriceDTO stdPriceDTO = objectMapper.readValue(results.get(i).toString(), MaterialStdPriceDTO.class);
                standardPriceDTOList.add(stdPriceDTO);
                amountOfSuccessfullMaps++;
            } catch (JsonProcessingException | JSONException e) {
                amountOfUnSuccessfullMaps++;
                log.debug(e.getMessage());
                throw new Error("Failed to process JSON", e.getCause());
            }
        }
        
        log.debug(String.format("Amount of successful maps: %d", amountOfSuccessfullMaps));
        log.debug(String.format("Amount of unsuccessful maps: %d", amountOfUnSuccessfullMaps));
        return standardPriceDTOList;
    }
    
    
}
