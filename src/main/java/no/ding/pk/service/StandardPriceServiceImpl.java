package no.ding.pk.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

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

import no.ding.pk.utils.RequestHeaderUtil;
import no.ding.pk.web.dto.MaterialDTO;

@Service
public class StandardPriceServiceImpl implements StandardPriceService {
    
    private static final Logger log = LoggerFactory.getLogger(StandardPriceServiceImpl.class);
    
    @Value("${sap.api.standard.price.url}")
    private final String standardPriceSapUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet";
    
    private String sapUsername;
    private String sapPassword;
    
    private ObjectMapper objectMapper;
    
    private InMemoryCache<String, String, MaterialDTO> inMemoryCache;
    
    @Autowired
    public StandardPriceServiceImpl(
    @Value(value = "${sap.username}") String sapUsername, 
    @Value("${sap.password}") String sapPassword,
    ObjectMapper objectMapper,
    InMemoryCache<String, String, MaterialDTO> inMemoryCache
    ) {
        this.sapUsername = sapUsername;
        this.sapPassword = sapPassword;
        this.objectMapper = objectMapper;
        this.inMemoryCache = inMemoryCache;
    }
    
    public List<MaterialDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg) {
        if(inMemoryCache.size(salesOffice) == 0 || inMemoryCache.isExpired()) {
            inMemoryCache.cleanUp();
            log.debug("Cache is empty or expired, fetching new items.");
            HttpClient client = HttpClient.newBuilder()
            .build();
            
            String filterQuery = String.format("Salgskontor eq '%s' and Salgsorg eq '%s' and Sone eq ''", salesOffice, salesOrg);
            
            log.debug(String.format("Filter query: %s", filterQuery));
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("$filter", filterQuery);
            params.add("$format", "json");
            
            UriComponents url = UriComponentsBuilder
            .fromUriString(standardPriceSapUrl)
            .queryParams(params)
            .build();
            
            log.debug("Created URL with URL prams: " + url.toUri().toString());
            
            HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(url.toUri())
            .header(HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword))
            .build();
            
            log.debug("Created request: " + request.toString());
            
            HttpResponse<String> response;
            
            try {
                response = client.send(request, BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new Error(e.getMessage(), e);
            }
            
            List<MaterialDTO> standardPriceDTOList = jsonToMaterialDTO(response);

            log.debug(String.format("Adding %d items to cache.", standardPriceDTOList.size()));
            for(MaterialDTO material : standardPriceDTOList) {
                StringBuffer objectKey = new StringBuffer();
                objectKey.append(material.getMaterial());

                if(!StringUtils.isBlank(material.getDeviceType())) {
                    objectKey.append("_").append(material.getDeviceType());
                }
                inMemoryCache.put(salesOffice, objectKey.toString(), material);
            }
            int amountAddedForSalesOffice = inMemoryCache.size(salesOffice);
            log.debug(String.format("Added %d items to cache.", amountAddedForSalesOffice));
            
            log.debug("Returning from new cache");
            return inMemoryCache.getAll(salesOffice);
        } else {
            return inMemoryCache.getAll(salesOffice);
        }
    }
    
    private List<MaterialDTO> jsonToMaterialDTO(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray results = jsonObject.getJSONObject("d").getJSONArray("results");
        log.debug(String.format("JSON array contains %d elements", results.length()));
        List<MaterialDTO> standardPriceDTOList = new ArrayList<>();

        int amountOfSuccessfullMaps = 0;
        int amountOfUnSuccessfullMaps = 0;
        for(int i = 0; i < results.length(); i++) {
            try {
                MaterialDTO stdPriceDTO = objectMapper.readValue(results.get(i).toString(), MaterialDTO.class);
                standardPriceDTOList.add(stdPriceDTO);
                amountOfSuccessfullMaps++;
            } catch (JsonProcessingException | JSONException e) {
                amountOfUnSuccessfullMaps++;
                throw new Error("Failed to process JSON", e.getCause());
            }
        }

        log.debug(String.format("Amount of successful maps: %d", amountOfSuccessfullMaps));
        log.debug(String.format("Amount of unsuccessful maps: %d", amountOfUnSuccessfullMaps));
        return standardPriceDTOList;
    }
}
