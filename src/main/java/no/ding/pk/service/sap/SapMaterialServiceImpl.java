package no.ding.pk.service.sap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.utils.LogicExpression;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.enums.LogicComparator;
import no.ding.pk.web.enums.LogicOperator;
import no.ding.pk.web.enums.MaterialField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SapMaterialServiceImpl implements SapMaterialService {
    private static final Logger log = LoggerFactory.getLogger(SapMaterialServiceImpl.class);

    private final String materialServiceUrl;
    private final SapHttpClient sapHttpClient;
    private final CacheManager cacheManager;

    private final LocalJSONUtils localJSONUtils;
    private final String DISTRIBUTION_CHANNEL_DOWN_STREAM = "01";

    private Map<String, MaterialDTO> sapMaterialCache;

    @Autowired
    public SapMaterialServiceImpl(@Value(value = "${PK_SAP_API_MATERIAL_URL}") String materialServiceUrl,
                                  SapHttpClient sapHttpClient,
                                  CacheManager cacheManager, LocalJSONUtils localJSONUtils) {
        this.materialServiceUrl = materialServiceUrl;
        this.sapHttpClient = sapHttpClient;
        this.cacheManager = cacheManager;
        this.localJSONUtils = localJSONUtils;
    }

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelay = 0L, fixedRate = 60 * 60 * 1000)
    public void getSapMaterialsFromSap() {
        log.debug("Warming up SAP material cache");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", MaterialField.SalesOrganization + " eq '100'");
        params.add("$top", "100000");
        params.add("$format", "json");

        HttpRequest getRequest = sapHttpClient.createGetRequest(materialServiceUrl, params);

        HttpResponse<String> response = sapHttpClient.getResponse(getRequest);
        String headerContentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);

        List<MaterialDTO> materialsForSalesOrg = new ArrayList<>();

        if (response.statusCode() == HttpStatus.OK.value() && StringUtils.contains(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {
            materialsForSalesOrg = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);

            this.sapMaterialCache = createMaterialMapFromList(materialsForSalesOrg);
        } else {
            log.debug("Response code was {}, but expected content type did not match. Expected content type is {}, got {}", response.statusCode(), MediaType.APPLICATION_JSON_VALUE, headerContentType);
        }

        Cache cache = cacheManager.getCache("sapMaterialCache");

        if (cache != null) {
            cache.putIfAbsent("100", materialsForSalesOrg);
        }

        log.debug("Cached {} amount of materials.", ((List<MaterialDTO>) cache.get("100").get()).size());
    }

    private static Map<String, MaterialDTO> createMaterialMapFromList(List<MaterialDTO> materialsForSalesOrg) {
        return materialsForSalesOrg.stream().collect(Collectors.toMap(MaterialDTO::getMaterial, Function.identity()));
    }

    @Cacheable(cacheNames = "sapMaterialCache", key = "{#material + '_' + #salesOrg}")
    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String salesOrg, String material) {
        LogicExpression materialExpression = LogicExpression.builder().field(MaterialField.Material).value(material).comparator(LogicComparator.Equal).build();
        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();

        Map<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(
                ImmutableMap.of(materialExpression, LogicOperator.And,
                        salesOrgExpression, LogicOperator.And));
        String filterQuery = createFilterQuery(queryMap);

        MultiValueMap<String, String> params = createParameterMap(filterQuery, 0, 5000, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        String headerContentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);

        if (response.statusCode() == HttpStatus.OK.value() && StringUtils.equals(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {
            MaterialDTO materialDTO = localJSONUtils.jsonStringToObject(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);
            log.debug("MaterialDTOList {}", materialDTO);

            return materialDTO;
        } else {
            log.debug("Response code was {}, but expected content type did not match. Expected content type is {}, got {}", response.statusCode(), MediaType.APPLICATION_JSON_VALUE, headerContentType);
        }

        return null;
    }

    @Cacheable(cacheNames = "sapMaterialCache", keyGenerator = "sapMaterialKeyGenerator")
    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(String salesOrg, String salesOffice, String material,
                                                                             String zone) {
        LogicExpression materialExpression = LogicExpression.builder().field(MaterialField.Material).value(material).comparator(LogicComparator.Equal).build();
        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();

        Map<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(
                ImmutableMap.of(materialExpression, LogicOperator.And,
                        salesOrgExpression, LogicOperator.And));
        String filterQuery = createFilterQuery(queryMap);

        MultiValueMap<String, String> params = createParameterMap(filterQuery, 0, 5000, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if (response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);
            log.debug("MaterialDTOList {}", materialDTOList.size());

            if(!materialDTOList.isEmpty()) {
                return materialDTOList.get(0);
            }
        }

        return null;
    }

    //@Cacheable(cacheNames = "sapMaterialCache", key = "#salesOrg")
    @Override
    public List<MaterialDTO> getAllMaterialsForSalesOrgBy(String salesOrg, Integer page, Integer pageSize) {
//        Cache cache = cacheManager.getCache("sapMaterialCache");
//
//        if(cache != null) {
//            if(cache.get(salesOrg) != null) {
//                return (List<MaterialDTO>) cache.get(salesOrg).get();
//            }
//        }

        List<String> cacheKeys = sapMaterialCache.keySet().stream().filter(s -> s.contains(salesOrg)).toList();

        if(!cacheKeys.isEmpty()) {
            List<MaterialDTO> materialDTOS = sapMaterialCache.entrySet().stream().filter(stringMaterialDTOEntry -> stringMaterialDTOEntry.getKey().contains(salesOrg)).map(Map.Entry::getValue).toList();

            if(!materialDTOS.isEmpty()) {
                return materialDTOS;
            }
        }

        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        String filterQuery = createFilterQuery(Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And)));

        MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        String headerContentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);

        if (response.statusCode() == HttpStatus.OK.value() && StringUtils.contains(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);
            log.debug("MaterialDTOList {}", materialDTOList.size());

            sapMaterialCache = createMaterialMapFromList(materialDTOList);

            return materialDTOList;
        } else {
            log.debug("Response code was {}, but expected content type did not match. Expected content type is {}, got {}", response.statusCode(), MediaType.APPLICATION_JSON_VALUE, headerContentType);
        }

        return new ArrayList<>();
    }

    private Integer getCountFromSap(String salesOrg) {
        String countUrl = String.format("%s/%s", materialServiceUrl, "$count");

        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        LinkedHashMap<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And));

        String filterQuery = createFilterQuery(queryMap);

        MultiValueMap<String, String> params = createParameterMap(filterQuery, null, null, null);

        HttpRequest request = sapHttpClient.createGetRequest(countUrl, params);
        HttpResponse<String> response = sapHttpClient.getResponse(request);

        log.debug("Count response: {}", response);

        String headerContentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);

        if(response.statusCode() == HttpStatus.OK.value() && StringUtils.equals(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {
            return Integer.valueOf(response.body());
        }

        return -1;
    }

    /**
     * Execute request to SAP Material service with filter query
     * @param filterQuery filter query
     * @param page page to start from
     * @param pageSize amount of elements per page
     * @return List of MaterialDTO
     */
    private List<MaterialDTO> requestSapMaterialService(String filterQuery, Integer page, Integer pageSize) {
        MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {

            return localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class);
        }

        log.debug("Response code {}", response.statusCode());

        return new ArrayList<>();
    }

    private static MultiValueMap<String, String> createParameterMap(String filterQuery, Integer page, Integer pageSize, String format) {
        MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        if(StringUtils.isNotBlank(format)) {
            params.add("$format", format);
        }

        if(page != null && pageSize != null) {
            Integer skipTokens = page * pageSize;

            params.add("$skiptoken", String.valueOf(skipTokens));
            params.add("$top", String.valueOf(pageSize));
        }
        return params;
    }

    private String createFilterQuery(Map<LogicExpression, LogicOperator> queryMap) {
        LogicExpression distributionChannelExpression = LogicExpression.builder()
                .field(MaterialField.DistributionChannel).value(DISTRIBUTION_CHANNEL_DOWN_STREAM).comparator(LogicComparator.Equal).build();
        queryMap.put(distributionChannelExpression, null);
        StringBuilder sb = new StringBuilder();

        for(Map.Entry<LogicExpression, LogicOperator> set : queryMap.entrySet()) {
            appendToQuery(sb, set.getKey());

            if(set.getValue() != null) {
                addLogicOperator(sb, set.getValue().getValue());
            }
        }

        return sb.toString();
    }

    private void addLogicOperator(StringBuilder sb, String operator) {
        if(!sb.isEmpty()) {
            if(StringUtils.endsWith(sb, "'")) {
                sb.append(String.format(" %s ", operator));
            }
        }
    }

    private void appendToQuery(StringBuilder sb, LogicExpression expression) {
        sb.append(String.format("%s %s '%s'", expression.getField(), expression.getComparator().getValue(), expression.getValue()));
    }
}
