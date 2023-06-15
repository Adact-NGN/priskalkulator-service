package no.ding.pk.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.enums.MaterialField;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StandardPriceServiceImpl implements StandardPriceService {
    
    private static final Logger log = LoggerFactory.getLogger(StandardPriceServiceImpl.class);
    
    @Value("${sap.api.standard.price.url}")
    private final String standardPriceSapUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet";

    private final ObjectMapper objectMapper;
    
    private final InMemory3DCache<String, String, MaterialStdPriceDTO> inMemoryCache;

    private final SapHttpClient sapHttpClient;

    private final SapMaterialService sapMaterialService;
    
    @Autowired
    public StandardPriceServiceImpl(
    ObjectMapper objectMapper,
    @Qualifier("standardPriceInMemoryCache") InMemory3DCache<String, String, MaterialStdPriceDTO> inMemoryCache,
    SapMaterialService sapMaterialService,
    SapHttpClient sapHttpClient) {
        this.objectMapper = objectMapper;
        this.inMemoryCache = inMemoryCache;
        this.sapHttpClient = sapHttpClient;
        this.sapMaterialService = sapMaterialService;
    }
    
    @Override
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg, String zone) {
        String filterQuery = createFilterQuery(salesOffice, salesOrg, null, zone);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");
        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);

        log.debug("Created request: " + request.toString());
        
        HttpResponse<String> response = sendRequest(request);

        log.debug("Response code: {}", response.statusCode());
        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> standardPriceDTOList = jsonToMaterialStdPriceDTO(response);

            if(StringUtils.isNotBlank(zone)) {
                standardPriceDTOList = standardPriceDTOList.stream().filter(p -> StringUtils.isNotBlank(p.getZone()) && p.getZone().equals(zone)).toList();
            } else {
                standardPriceDTOList = standardPriceDTOList.stream().filter(p -> StringUtils.isBlank(p.getZone())).toList();
            }

            List<MaterialDTO> allMaterialsForSalesOrg = sapMaterialService.getAllMaterialsForSalesOrg(salesOrg, 0, 5000);

            if(StringUtils.isBlank(zone)) {
                List<MaterialDTO> nonZonedMaterialsDTO = allMaterialsForSalesOrg.stream().filter(p -> !"Sone differensiert".equals(p.getSubCategoryDescription())).toList();
                List<String> nonZonedMaterialNumbers = nonZonedMaterialsDTO.stream().map(MaterialDTO::getMaterial).toList();

                standardPriceDTOList = standardPriceDTOList.stream().filter(p -> nonZonedMaterialNumbers.contains(p.getMaterial())).toList();
            }

            Map<String, MaterialDTO> materialDTOMap = createMaterialDTOMap(allMaterialsForSalesOrg);

            addMaterialDataToStandardPrice(standardPriceDTOList, materialDTOMap);

            return standardPriceDTOList;
        }
        
        return new ArrayList<>();
    }

    private static void addMaterialDataToStandardPrice(List<MaterialStdPriceDTO> standardPriceDTOList, Map<String, MaterialDTO> materialDTOMap) {
        for(MaterialStdPriceDTO priceDTO : standardPriceDTOList) {
            if(materialDTOMap.containsKey(priceDTO.getMaterial())) {
                priceDTO.setMaterialData(materialDTOMap.get(priceDTO.getMaterial()));
            }
        }
    }

    private static Map<String, MaterialDTO> createMaterialDTOMap(List<MaterialDTO> allMaterialsForSalesOrg) {
        Map<String, MaterialDTO> materialDTOMap = new HashMap<>();
        for(MaterialDTO materialDTO : allMaterialsForSalesOrg) {
            materialDTOMap.put(materialDTO.getMaterial(), materialDTO);
        }
        return materialDTOMap;
    }

    @Override
    public MaterialPrice getStandardPriceForMaterial(String materialNumber, String salesOrg, String salesOffice) {
        String filterQuery = createFilterQuery(salesOffice, salesOrg, materialNumber, null);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");
        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> materialStdPriceDTO = jsonToMaterialStdPriceDTO(response);

            if(materialStdPriceDTO.isEmpty()) {
                return null;
            }

            List<MaterialDTO> allMaterialsForSalesOrg = sapMaterialService.getAllMaterialsForSalesOrg(salesOrg, 0, 5000);

            Map<String, MaterialDTO> materialDTOMap = createMaterialDTOMap(allMaterialsForSalesOrg);

            addMaterialDataToStandardPrice(materialStdPriceDTO, materialDTOMap);

            return materialDtoToMaterialPrice(materialNumber, materialStdPriceDTO.get(0));
        }
        
        return null;
    }

    @Override
    public List<MaterialStdPriceDTO> getStandardPriceForSalesOrgSalesOfficeAndMaterial(String salesOrg, String salesOffice, String material, String zone) {
        String filterQuery = createFilterQuery(salesOffice, salesOrg, material, zone);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");

        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> priceDTOS = jsonToMaterialStdPriceDTO(response);

            if(!priceDTOS.isEmpty()) {
                return priceDTOS;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<MaterialStdPriceDTO> getStandardPriceForMaterialInList(String salesOrg, String salesOffice, List<String> materialNumbers) {
        List<MaterialStdPriceDTO> materialStdPriceDTOS = new ArrayList<>();

        for(String material : materialNumbers) {

            List<MaterialStdPriceDTO> standardPriceForMaterial = getStandardPriceDTO(salesOrg, salesOffice, material);
            
            materialStdPriceDTOS.addAll(standardPriceForMaterial);

            List<MaterialDTO> allMaterialsForSalesOrg = sapMaterialService.getAllMaterialsForSalesOrg(salesOrg, 0, 5000);

            Map<String, MaterialDTO> materialDTOMap = createMaterialDTOMap(allMaterialsForSalesOrg);

            addMaterialDataToStandardPrice(materialStdPriceDTOS, materialDTOMap);
        }
        return materialStdPriceDTOS;
    }

    private void initiateCacheBuild(String salesOrg, String salesOffice) {
        log.debug("Getting Materials data for sales org {} and sales office {}", salesOrg, salesOffice);
        String filterQuery = createFilterQuery(salesOffice, salesOrg);

        log.debug(String.format("Filter query: %s", filterQuery));

        buildUpStandardPriceCache(salesOffice, filterQuery);

        log.debug("Returning from new cache");
    }

    private MaterialPrice materialDtoToMaterialPrice(String materialNumber, MaterialStdPriceDTO materialStdPriceDTO) {
        return MaterialPrice.builder()
        .materialNumber(materialNumber)
        .standardPrice(materialStdPriceDTO.getStandardPrice())
        .validFrom(materialStdPriceDTO.getValidFrom())
        .validTo(materialStdPriceDTO.getValidTo())
        .build();
    }
    
    private void buildUpStandardPriceCache(String salesOffice, String filterQuery) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");
        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);
        
        log.debug("Created request: " + request.toString());
        
        HttpResponse<String> response = sendRequest(request);

        log.debug("Response code: {}", response.statusCode());
        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> standardPriceDTOList = jsonToMaterialStdPriceDTO(response);

            addMaterialsToCache(salesOffice, standardPriceDTOList);
        } else {
            log.debug("Response code was {}", response.statusCode());
        }
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg) {
        return createFilterQuery(salesOffice, salesOrg, null, null);
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg, String materialNumber, String zone) {
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

        if(StringUtils.isNotBlank(zone)) {
            if(zone.length() < 2) {
                zone = "0" + zone;
            }
            filterQuery.append(String.format(" and %s eq '%s'", MaterialField.SalesZone.getValue(), zone));
        } else {
            filterQuery.append(String.format(" and %s eq '%s'", MaterialField.SalesZone.getValue(), ""));
        }
        return filterQuery.toString();
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
            StringBuilder tempObjectKey = new StringBuilder();
            tempObjectKey.append(material.getMaterial());
            
            if(!StringUtils.isBlank(material.getZone())) {
                tempObjectKey.append("_").append(material.getZone());
            } else {
                tempObjectKey.append("_X");
            }

            if(!StringUtils.isBlank(material.getDeviceType())) {
                tempObjectKey.append("_").append(material.getDeviceType());
            } else {
                tempObjectKey.append("_X");
            }

            String objectKey = tempObjectKey.toString();
            inMemoryCache.put(salesOffice, objectKey, material);
        }
        int amountAddedForSalesOffice = inMemoryCache.size(salesOffice);
        log.debug(String.format("Added %d items to cache.", amountAddedForSalesOffice));
    }
    
    private List<MaterialStdPriceDTO> jsonToMaterialStdPriceDTO(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        
        if(jsonObject.has("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            
            log.debug("code: " + errorObject.getString("code"));
            log.debug("message" + errorObject.getJSONObject("message").getString("value"));
        }
        
        JSONArray results = jsonObject.getJSONObject("d").getJSONArray("results");
        log.debug(String.format("JSON array contains %d elements", results.length()));
        List<MaterialStdPriceDTO> standardPriceDTOList = new ArrayList<>();
        
        int amountOfSuccessfulMaps = 0;
        for(int i = 0; i < results.length(); i++) {
            MaterialStdPriceDTO stdPriceDTO = mapJsonObjectToEntity(results.getJSONObject(i));
            amountOfSuccessfulMaps++;
            standardPriceDTOList.add(stdPriceDTO);
        }
        
        log.debug(String.format("Amount of successful maps: %d", amountOfSuccessfulMaps));
        return standardPriceDTOList;
    }

    private MaterialStdPriceDTO mapJsonObjectToEntity(JSONObject jsonObject) {
        try {
            return objectMapper.readValue(jsonObject.toString(), MaterialStdPriceDTO.class);
        } catch (JsonProcessingException | JSONException e) {
            log.debug(e.getMessage());
            throw new Error("Failed to process JSON", e.getCause());
        }
    }

    @Override
    public List<MaterialStdPriceDTO> getStandardPriceDTO(String salesOrg, String salesOffice, String material) {

        String filterQuery = createFilterQuery(salesOffice, salesOrg, material, null);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");
        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);
        
        log.debug("Created request: " + request.toString());
        
        HttpResponse<String> response = sendRequest(request);

        log.debug("Response code: {}", response.statusCode());
        if(response.statusCode() == HttpStatus.OK.value()) {
            return jsonToMaterialStdPriceDTO(response);
        }
        return new ArrayList<>();
    }
    
    
}
