package no.ding.pk.service.sap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import no.ding.pk.service.cache.InMemory3DCache;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

//@EnableAsync
@Service
public class SapMaterialServiceImpl implements SapMaterialService {
    private static final Logger log = LoggerFactory.getLogger(SapMaterialServiceImpl.class);

    private final String materialServiceUrl;
    private final SapHttpClient sapHttpClient;

    private final InMemory3DCache<String, String, MaterialDTO> inMemoryCache;

    private final LocalJSONUtils localJSONUtils;
    private final String DISTRIBUTION_CHANNEL_DOWN_STREAM = "01";

    @Autowired
    public SapMaterialServiceImpl(@Value(value = "${PK_SAP_API_MATERIAL_URL}") String materialServiceUrl,
                                  SapHttpClient sapHttpClient,
                                  LocalJSONUtils localJSONUtils,
                                  @Qualifier("materialInMemoryCache") InMemory3DCache<String, String, MaterialDTO> inMemoryCache) {
        this.materialServiceUrl = materialServiceUrl;
        this.sapHttpClient = sapHttpClient;
        this.localJSONUtils = localJSONUtils;
        this.inMemoryCache = inMemoryCache;
    }

    @Cacheable(cacheNames = "sapMaterialCache", keyGenerator = "sapMaterialKeyGenerator")
    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String material, String salesOrg) {
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

    @Async("asyncTaskExecutor")
    @Scheduled(initialDelay = 0L, fixedRate = 60 * 60 * 1000)
    public void getSapMaterialsFromSap() {
        log.debug("Warming up SAP material cache");
        getAllMaterialsForSalesOrgByZone("100", 0, 1000000);
    }

    @Cacheable(cacheNames = "sapMaterialCache", key = "#salesOrg")
    @Override
    public List<MaterialDTO> getAllMaterialsForSalesOrgByZone(String salesOrg, Integer page, Integer pageSize) {
        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        String filterQuery = createFilterQuery(Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And)));

        MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        String headerContentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);

        if (response.statusCode() == HttpStatus.OK.value() && StringUtils.equals(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);
            log.debug("MaterialDTOList {}", materialDTOList.size());
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

    @Override
    public List<MaterialDTO> getAllMaterialsForSalesOrgBy(String salesOrg, String zone, Integer page, Integer pageSize) {
        Integer materialCount = getCountFromSap(salesOrg);
        log.debug("Got amount of materials for sales org: {} amount: {} vs cache {}", salesOrg, materialCount, inMemoryCache.size(salesOrg));

        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        HashMap<LogicExpression, LogicOperator> queryMap =
                Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And));

        String filterQuery = createFilterQuery(queryMap);

        log.debug("Filter query: {}", filterQuery);
        return requestSapMaterialService(filterQuery, page, pageSize);
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
        if(sb.length() > 0) {
            if(StringUtils.endsWith(sb, "'")) {
                sb.append(String.format(" %s ", operator));
            }
        }
    }

    private void appendToQuery(StringBuilder sb, LogicExpression expression) {
        sb.append(String.format("%s %s '%s'", expression.getField(), expression.getComparator().getValue(), expression.getValue()));
    }
}
