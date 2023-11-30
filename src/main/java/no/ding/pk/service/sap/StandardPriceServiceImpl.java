package no.ding.pk.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.cache.StandardPriceKeyGenerator;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.enums.MaterialField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StandardPriceServiceImpl implements StandardPriceService {
    
    private static final Logger log = LoggerFactory.getLogger(StandardPriceServiceImpl.class);
    
    private final String standardPriceSapUrl;

    private final ObjectMapper objectMapper;

    private final SapHttpClient sapHttpClient;

    private final SapMaterialService sapMaterialService;
    private final ModelMapper modelMapper;

    private final SalesOrgService salesOrgService;

    private final CacheManager cacheManager;
    private final String getStdPricesForSalesOfficeAndSalesOrgName = "getStdPricesForSalesOfficeAndSalesOrg";
    private final String getStdPricesForSalesOfficeAndSalesOrgMapName = "getStdPricesForSalesOfficeAndSalesOrgMap";
    private final String stdPriceCacheName = "stdPriceCache";
    private final StandardPriceKeyGenerator standardPriceKeyGenerator;
    private final SimpleKeyGenerator simpleKeyGenerator;

    @Autowired
    public StandardPriceServiceImpl(
            @Value("${sap.api.standard.price.url}")
            String standardPriceSapUrl,
            ObjectMapper objectMapper,
            SapMaterialService sapMaterialService,
            SapHttpClient sapHttpClient,
            @Qualifier("modelMapperV2") ModelMapper modelMapper,
            SalesOrgService salesOrgService,
            CacheManager cacheManager) {
        this.standardPriceSapUrl = standardPriceSapUrl;
        this.objectMapper = objectMapper;
        this.sapHttpClient = sapHttpClient;
        this.sapMaterialService = sapMaterialService;
        this.modelMapper = modelMapper;
        this.salesOrgService = salesOrgService;

        this.cacheManager = cacheManager;
        standardPriceKeyGenerator = new StandardPriceKeyGenerator();
        simpleKeyGenerator = new SimpleKeyGenerator();
    }

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelay = 0L, fixedRate = 60 * 60 * 1000)
    public void getStandardPricesFromSap() {
        log.debug("Warming up Standard price cache");

        StopWatch watch = new StopWatch();
        watch.start();
        List<MaterialStdPriceDTO> allStandardPrices = getAllStandardPrices();
        watch.stop();
        log.debug("Time used to get all standard prices: {} sec", watch.getTime() / 1000);

        Map<String, List<MaterialStdPriceDTO>> collect = new HashMap<>();
        Map<String, List<MaterialStdPriceDTO>> stdPriceCacheMap = new HashMap<>();

        for(MaterialStdPriceDTO stdPriceDTO : allStandardPrices) {
            Object stdPriceForSalesOfficeAndSalesOrgKey = standardPriceKeyGenerator.generate(null, null, List.of(
                    stdPriceDTO.getSalesOffice(), stdPriceDTO.getSalesOrg(), stdPriceDTO.getZone()
            ));

            if(!collect.containsKey(stdPriceForSalesOfficeAndSalesOrgKey.toString())) {
                collect.put(stdPriceForSalesOfficeAndSalesOrgKey.toString(), new ArrayList<>());
            }

            collect.get(stdPriceForSalesOfficeAndSalesOrgKey.toString()).add(stdPriceDTO);

            Object simpleKey = simpleKeyGenerator.generate(null, null, List.of(stdPriceDTO.getSalesOrg(), stdPriceDTO.getSalesOffice(), stdPriceDTO.getMaterial()));

            if(!stdPriceCacheMap.containsKey(stdPriceForSalesOfficeAndSalesOrgKey.toString())) {
                stdPriceCacheMap.put(simpleKey.toString(), new ArrayList<>());
            }

            stdPriceCacheMap.get(simpleKey.toString()).add(stdPriceDTO);
        }

        Cache getStdPricesForSalesOfficeAndSalesOrg = cacheManager.getCache(this.getStdPricesForSalesOfficeAndSalesOrgName);
        if (getStdPricesForSalesOfficeAndSalesOrg != null) {
            getStdPricesForSalesOfficeAndSalesOrg.invalidate();
        }
        Cache getStdPricesForSalesOfficeAndSalesOrgMap = cacheManager.getCache(this.getStdPricesForSalesOfficeAndSalesOrgMapName);
        if (getStdPricesForSalesOfficeAndSalesOrgMap != null) {
            getStdPricesForSalesOfficeAndSalesOrgMap.invalidate();
        }
        for (Map.Entry<String, List<MaterialStdPriceDTO>> stringListEntry : collect.entrySet()) {
            if (getStdPricesForSalesOfficeAndSalesOrg != null) {
                getStdPricesForSalesOfficeAndSalesOrg.putIfAbsent(stringListEntry.getKey(), stringListEntry.getValue());
            }
            if (getStdPricesForSalesOfficeAndSalesOrgMap != null) {
                getStdPricesForSalesOfficeAndSalesOrgMap.putIfAbsent(stringListEntry.getKey(), stringListEntry.getValue());
            }
        }

        Cache stdPriceCache = cacheManager.getCache(this.stdPriceCacheName);
        if (stdPriceCache != null) {
            stdPriceCache.invalidate();
        }
        for(Map.Entry<String, List<MaterialStdPriceDTO>> stdPriceCacheItem : stdPriceCacheMap.entrySet()) {
            if (stdPriceCache != null) {
                stdPriceCache.putIfAbsent(stdPriceCacheItem.getKey(), stdPriceCacheItem.getValue());
            }
        }

        log.debug("Standard Price Cache done");
    }


    @Override
    //@Cacheable(cacheNames = {getStdPricesForSalesOfficeAndSalesOrgName}, keyGenerator = "stdPriceKeyGenerator")
    public List<MaterialStdPriceDTO> getStdPricesForSalesOfficeAndSalesOrg(String salesOrg, String salesOffice, String zone) {
        Object cacheKey = standardPriceKeyGenerator.generate(null, null, null, List.of(salesOffice, salesOrg, zone));
        Cache cache = cacheManager.getCache(getStdPricesForSalesOfficeAndSalesOrgName);

        if(cache != null) {
            if(cache.get(cacheKey) != null) {
                return (List<MaterialStdPriceDTO>) cache.get(cacheKey).get();
            }
        }
        String filterQuery = createFilterQuery(salesOffice, salesOrg, null, null);

        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        log.debug("Response code: {}", response.statusCode());
        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> standardPriceDTOList = jsonToMaterialStdPriceDTO(response);

            standardPriceDTOList = filterStdPricesByZone(zone, standardPriceDTOList);

            List<MaterialDTO> allMaterialsForSalesOrg = sapMaterialService.getAllMaterialsForSalesOrgBy(salesOrg, 0, 5000);

            if(allMaterialsForSalesOrg.isEmpty()) {
                return standardPriceDTOList;
            }

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

    //@Cacheable(cacheNames = {getStdPricesForSalesOfficeAndSalesOrgMapName}, keyGenerator = "stdPriceKeyGenerator")
    @Override
    public Map<String, MaterialPrice> getStandardPriceForSalesOrgAndSalesOfficeMap(String salesOrg, String salesOffice, String zone) {

        Object cacheKey = standardPriceKeyGenerator.generate(null, null, null, List.of(salesOrg, salesOffice, zone));
        Cache cache = cacheManager.getCache(getStdPricesForSalesOfficeAndSalesOrgMapName);

        if(cache != null) {
            if(cache.get(cacheKey) != null) {
                return (Map<String, MaterialPrice>) cache.get(cacheKey).get();
            }
        }

        String formattedZone = getFormattedZone(zone);
        log.debug("Getting standard prices for sales org {}, sales office {}, zone {}", salesOrg, salesOffice, formattedZone);
        String filterQuery = createFilterQuery(salesOffice, salesOrg, null, null);
        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> materialStdPriceDTO = jsonToMaterialStdPriceDTO(response);

            if(StringUtils.isNotBlank(formattedZone)) {
                List<MaterialStdPriceDTO> filteredMaterialStdPrice = new ArrayList<>();
                for (MaterialStdPriceDTO materialStdPrice : materialStdPriceDTO) {
                    if (StringUtils.isNotBlank(materialStdPrice.getZone()) && materialStdPrice.getZone().equals(formattedZone)) {
                        filteredMaterialStdPrice.add(materialStdPrice);
                    }
                }

                materialStdPriceDTO = filteredMaterialStdPrice;
            }

            if(materialStdPriceDTO.isEmpty()) {
                log.debug("Material standard price is empty.");
                return new HashMap<>();
            }

            List<MaterialDTO> allMaterialsForSalesOrg = sapMaterialService.getAllMaterialsForSalesOrgBy(salesOrg, 0, 5000);

            Map<String, MaterialDTO> materialDTOMap = createMaterialDTOMap(allMaterialsForSalesOrg);

            addMaterialDataToStandardPrice(materialStdPriceDTO, materialDTOMap);

            List<MaterialPrice> materialPrices = List.of(modelMapper.map(materialStdPriceDTO, MaterialPrice[].class));

            return materialPrices.stream().collect(Collectors.toMap(MaterialPrice::getUniqueMaterialNumber, Function.identity()));
        }
        
        return new HashMap<>();
    }

    private static String getFormattedZone(String zone) {
        return StringUtils.isNotBlank(zone) ? String.format("0%d", Integer.valueOf(zone)) : null;
    }

    @Cacheable(cacheNames = {getStdPricesForSalesOfficeAndSalesOrgName}, keyGenerator = "stdPriceKeyGenerator")
    @Override
    public List<MaterialStdPriceDTO> getStandardPriceForSalesOrgSalesOfficeAndMaterial(String salesOrg, String salesOffice, String material, String zone) {

        String filterQuery = createFilterQuery(salesOffice, salesOrg, material, null);

        HttpResponse<String> response = prepareAndPerformSapRequest(filterQuery);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialStdPriceDTO> priceDTOS = jsonToMaterialStdPriceDTO(response);

            priceDTOS = filterStdPricesByZone(zone, priceDTOS);

            priceDTOS = priceDTOS.stream().filter(materialStdPriceDTO -> materialStdPriceDTO.getSalesOffice().equals(salesOffice) && materialStdPriceDTO.getMaterial().equals(material)).collect(Collectors.toList());

            if(!priceDTOS.isEmpty()) {
                return priceDTOS;
            }
        }

        log.debug("Could not get any standard prices.");

        return new ArrayList<>();
    }

    private String createSalesOfficeMaterialNumberLookupKey(String salesOffice, String material, String zone, CharSequence deviceType) {
        StringBuilder sb = new StringBuilder();
        sb.append(salesOffice).append("_").append(material);

        if(StringUtils.isNotBlank(zone)) {
            sb.append("_").append(zone);
        }

        if(StringUtils.isNotBlank(deviceType)) {
            sb.append("_").append(deviceType);
        }

        return sb.toString();
    }

    private HttpResponse<String> prepareAndPerformSapRequest(String filterQuery) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");

        HttpRequest request = sapHttpClient.createGetRequest(standardPriceSapUrl, params);

        log.debug("Created request: " + request.toString());

        return sapHttpClient.getResponse(request);
    }

    private MaterialPrice materialDtoToMaterialPrice(String materialNumber, MaterialStdPriceDTO materialStdPriceDTO) {
        return MaterialPrice.builder(materialStdPriceDTO.getSalesOrg(), materialStdPriceDTO.getSalesOffice(),
                        materialNumber, materialStdPriceDTO.getDeviceType(), materialStdPriceDTO.getZone())
                .deviceType(materialStdPriceDTO.getDeviceType())
                .standardPrice(materialStdPriceDTO.getStandardPrice())
                .validFrom(materialStdPriceDTO.getValidFrom())
                .validTo(materialStdPriceDTO.getValidTo())
                .pricingUnit(Integer.valueOf(materialStdPriceDTO.getPricingUnit()))
                .quantumUnit(materialStdPriceDTO.getQuantumUnit())
                .build();
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg) {
        return createFilterQuery(salesOffice, salesOrg, null, null);
    }
    
    private String createFilterQuery(String salesOffice, String salesOrg, String materialNumber, String deviceType) {
        StringBuilder filterQuery = new StringBuilder();
        filterQuery.append(
                String.format("%s eq '%s' and %s eq '%s' and %s eq ''",
                        MaterialField.SalesOffice.getValue(), salesOffice,
                        MaterialField.SalesOrganization.getValue(), salesOrg,
                        MaterialField.MaterialExpired.getValue()
                ));

        addMaterialNumber(materialNumber, filterQuery);

        addDeviceType(deviceType, filterQuery, "and %s eq '%s'", MaterialField.DeviceCategory);
        return filterQuery.toString();
    }

    private static void addDeviceType(String deviceType, StringBuilder filterQuery, String format, MaterialField deviceCategory) {
        if (StringUtils.isNotBlank(deviceType)) {
            filterQuery.append(String.format(format, deviceCategory.getValue(), deviceType));
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

    //@Cacheable(stdPriceCacheName)
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

    public List<MaterialStdPriceDTO> getAllStandardPrices() {
        String filterQuery = "SalesOrganization eq '100'";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$top", "1000000");
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
