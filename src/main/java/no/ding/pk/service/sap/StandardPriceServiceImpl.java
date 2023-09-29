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
import org.modelmapper.ModelMapper;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StandardPriceServiceImpl implements StandardPriceService {
    
    private static final Logger log = LoggerFactory.getLogger(StandardPriceServiceImpl.class);
    
    private String standardPriceSapUrl;

    private final ObjectMapper objectMapper;
    
    private final InMemory3DCache<String, String, MaterialStdPriceDTO> inMemoryCache;

    private final SapHttpClient sapHttpClient;

    private final SapMaterialService sapMaterialService;
    private final ModelMapper modelMapper;

    @Autowired
    public StandardPriceServiceImpl(
            @Value("${sap.api.standard.price.url}")
            String standardPriceSapUrl,
            ObjectMapper objectMapper,
            @Qualifier("standardPriceInMemoryCache") InMemory3DCache<String, String, MaterialStdPriceDTO> inMemoryCache,
            SapMaterialService sapMaterialService,
            SapHttpClient sapHttpClient, @Qualifier("modelMapperV2") ModelMapper modelMapper) {
        this.standardPriceSapUrl = standardPriceSapUrl;
        this.objectMapper = objectMapper;
        this.inMemoryCache = inMemoryCache;
        this.sapHttpClient = sapHttpClient;
        this.sapMaterialService = sapMaterialService;
        this.modelMapper = modelMapper;
    }
    
    @Override
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg, String zone) {
        String filterQuery = createFilterQuery(salesOffice, salesOrg, null, zone, null);

        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        log.debug("Response code: {}", response.statusCode());
        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> standardPriceDTOList = jsonToMaterialStdPriceDTO(response);

            standardPriceDTOList = filterStdPricesByZone(zone, standardPriceDTOList);

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

    private static List<MaterialStdPriceDTO> filterStdPricesByZone(String zone, List<MaterialStdPriceDTO> standardPriceDTOList) {
        if(standardPriceDTOList == null) {
            return new ArrayList<>();
        }
        if(StringUtils.isNotBlank(zone)) {
            standardPriceDTOList = standardPriceDTOList.stream().filter(p -> StringUtils.isNotBlank(p.getZone()) && p.getZone().equals(zone)).toList();
        } else {
            standardPriceDTOList = standardPriceDTOList.stream().filter(p -> StringUtils.isBlank(p.getZone())).toList();
        }
        return standardPriceDTOList;
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
    public Map<String, MaterialPrice> getStandardPriceForSalesOrgAndSalesOfficeMap(String salesOrg, String salesOffice, String zone) {
        String filterQuery = createFilterQuery(salesOffice, salesOrg, null, zone, null);
        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> materialStdPriceDTO = jsonToMaterialStdPriceDTO(response);

            if(materialStdPriceDTO.isEmpty()) {
                log.debug("Material standard price is empty.");
                return null;
            }

            List<MaterialDTO> allMaterialsForSalesOrg = sapMaterialService.getAllMaterialsForSalesOrg(salesOrg, 0, 5000);

            Map<String, MaterialDTO> materialDTOMap = createMaterialDTOMap(allMaterialsForSalesOrg);

            addMaterialDataToStandardPrice(materialStdPriceDTO, materialDTOMap);

            List<MaterialPrice> materialPrices = List.of(modelMapper.map(materialStdPriceDTO, MaterialPrice[].class));

            return materialPrices.stream().collect(Collectors.toMap(MaterialPrice::getMaterialNumber, Function.identity()));
        }
        
        return new HashMap<>();
    }

    @Override
    public List<MaterialStdPriceDTO> getStandardPriceForSalesOrgSalesOfficeAndMaterial(String salesOrg, String salesOffice, String material, String zone) {
        String filterQuery = createFilterQuery(salesOffice, salesOrg, material, zone, null);

        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> priceDTOS = jsonToMaterialStdPriceDTO(response);

            priceDTOS = filterStdPricesByZone(zone, priceDTOS);

            if(!priceDTOS.isEmpty()) {
                return priceDTOS;
            }
        }

        log.debug("Could not get any standard prices.");

        return new ArrayList<>();
    }

    private HttpResponse<String> prepareAndPerformSapRequest(String filterQuery) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");

        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);

        log.debug("Created request: " + request.toString());

        return sapHttpClient.getResponse(request);
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
                .deviceType(materialStdPriceDTO.getDeviceType())
                .standardPrice(materialStdPriceDTO.getStandardPrice())
                .validFrom(materialStdPriceDTO.getValidFrom())
                .validTo(materialStdPriceDTO.getValidTo())
                .pricingUnit(Integer.valueOf(materialStdPriceDTO.getPricingUnit()))
                .quantumUnit(materialStdPriceDTO.getQuantumUnit())
                .build();
    }
    
    private void buildUpStandardPriceCache(String salesOffice, String filterQuery) {
        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        log.debug("Response code: {}", response.statusCode());
        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> standardPriceDTOList = jsonToMaterialStdPriceDTO(response);

            addMaterialsToCache(salesOffice, standardPriceDTOList);
        } else {
            log.debug("Response code was {}", response.statusCode());
        }
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg) {
        return createFilterQuery(salesOffice, salesOrg, null, null, null);
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg, String materialNumber, String zone, String deviceType) {
        StringBuilder filterQuery = new StringBuilder();
        filterQuery.append(
        String.format("%s eq '%s' and %s eq '%s' and %s eq ''", 
        MaterialField.SalesOffice.getValue(), salesOffice, 
        MaterialField.SalesOrganization.getValue(), salesOrg, 
        MaterialField.MaterialExpired.getValue()
        ));

        addMaterialNumber(materialNumber, filterQuery);

        addZone(zone, filterQuery);

        addDeviceType(deviceType, filterQuery, "and %s eq '%s'", MaterialField.DeviceCategory);
        return filterQuery.toString();
    }

    private static void addDeviceType(String deviceType, StringBuilder filterQuery, String format, MaterialField deviceCategory) {
        if (StringUtils.isNotBlank(deviceType)) {
            filterQuery.append(String.format(format, deviceCategory.getValue(), deviceType));
        }
    }

    private static void addZone(String zone, StringBuilder filterQuery) {
        if(StringUtils.isNotBlank(zone)) {
            if(zone.length() < 2) {
                zone = "0" + zone;
            }
            filterQuery.append(String.format(" and %s eq '%s'", MaterialField.SalesZone.getValue(), zone));
        } else {
            filterQuery.append(String.format(" and %s eq '%s'", MaterialField.SalesZone.getValue(), ""));
        }
    }

    private static void addMaterialNumber(String materialNumber, StringBuilder filterQuery) {
        addDeviceType(materialNumber, filterQuery, " and %s eq '%s'", MaterialField.Material);
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

        String filterQuery = createFilterQuery(salesOffice, salesOrg, material, null, null);

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
