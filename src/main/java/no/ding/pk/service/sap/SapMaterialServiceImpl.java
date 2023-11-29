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
import org.springframework.http.HttpStatus;
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

//    @EventListener(ApplicationReadyEvent.class)
//    @Async
//    @Scheduled(cron = "0 0 * * *", zone = "Europe/Paris")
    public void updateMaterialCache() {
        log.debug("Starting to populate Material cache.");
        List<MaterialDTO> materials = getAllMaterialsForSalesOrgBy("100", 0, 5000);
        log.debug("Got {} amount of materials.", materials.size());
        materials.forEach(materialDTO -> inMemoryCache.put("100", materialDTO.getMaterial(), materialDTO));
        log.debug("Added materials to cache. Cache size is: {}", inMemoryCache.size("100"));
    }


    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String material, String salesOrg) {
        if(inMemoryCache.size(salesOrg) == 0 || inMemoryCache.isExpired()) {
            log.debug("Initiating cache build for Sap Materials.");
            initiateCacheBuild(salesOrg, material);
        }

        if(!inMemoryCache.contains(salesOrg, material)) {
            log.debug("Cache does not contain material {} for sales org {}. Updating", material, salesOrg);
            updateMaterialCache(salesOrg, material, null, null);
        }

        return inMemoryCache.get(salesOrg, material);
    }

    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(String material, String salesOrg,
                                                                            String salesOffice, String zone) {
        LogicExpression materialExpression = LogicExpression.builder().field(MaterialField.Material).value(material).comparator(LogicComparator.Equal).build();
        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();

        Map<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(
                ImmutableMap.of(materialExpression, LogicOperator.And,
                        salesOrgExpression, LogicOperator.And));
        String filterQuery = createFilterQuery(queryMap);

        if(!inMemoryCache.contains(salesOrg, material)) {
            log.debug("Cache is missing the material.");

            updateMaterialCache(salesOrg, filterQuery);

            return inMemoryCache.get(salesOrg, material);
        }

        log.debug("Got material {} sales org {}", material, salesOrg);
        return inMemoryCache.get(salesOrg, material);
    }

    private void initiateCacheBuild(String salesOrg, String material) {
        log.debug("Getting Material data for {}", material);
        LogicExpression materialExpression = LogicExpression.builder().field(MaterialField.Material).value(material).comparator(LogicComparator.Equal).build();
        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        Map<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(ImmutableMap.of(materialExpression, LogicOperator.And,
                salesOrgExpression, LogicOperator.And));
        String filterQuery = createFilterQuery(queryMap);

        log.debug(String.format("Filter query: %s", filterQuery));

        buildMaterialCache(salesOrg, filterQuery, null, null);

        log.debug("Returning from new cache");
    }

    @Override
    public List<MaterialDTO> getAllMaterialsForSalesOrgBy(String salesOrg, Integer page, Integer pageSize) {
        // Get material size count from SAP
        Integer materialCount = getCountFromSap(salesOrg);
        log.debug("Got amount of materials for sales org: {} amount: {} vs cache {}", salesOrg, materialCount, inMemoryCache.size(salesOrg));

        if(materialCount != inMemoryCache.size(salesOrg)) {
            LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
            String filterQuery = createFilterQuery(Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And)));

            MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

            HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

            log.debug("Created request: " + request.toString());

            HttpResponse<String> response = sapHttpClient.getResponse(request);

            if (response.statusCode() == HttpStatus.OK.value()) {
                List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class);
                log.debug("MaterialDTOList {}", materialDTOList.size());
                return materialDTOList;
            }
        }

        return inMemoryCache.getAllInList(salesOrg);
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

        if(response.statusCode() == HttpStatus.OK.value()) {
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

    private void buildMaterialCache(String salesOrg, String filterQuery, Integer page, Integer pageSize) {
        log.debug("Building cache");

        updateMaterialCache(salesOrg, filterQuery, page, pageSize);
    }

    private void updateMaterialCache(String salesOrg, String filterQuery) {
        log.debug("Got sales org {}, query {}", salesOrg, filterQuery);

        updateMaterialCache(salesOrg, filterQuery, null, null);
    }

    private void updateMaterialCache(String salesOrg, String filterQuery, Integer page, Integer pageSize) {
        log.debug("Update material cache with sales org {}", salesOrg);

        MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class);

            addMaterialsToCache(salesOrg, materialDTOList);
        } else {
            log.debug("Response code {}", response.statusCode());
        }
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

    private void addMaterialsToCache(String salesOrg, List<MaterialDTO> materialDTOs) {
        log.debug("Adding {} items to cache.", materialDTOs.size());

        for(MaterialDTO material : materialDTOs) {
            inMemoryCache.put(salesOrg, material.getMaterial(), material);
        }

        log.debug("Added {} items to cache.", inMemoryCache.size(salesOrg));
    }

    private String createFilterQuery(Map<LogicExpression, LogicOperator> queryMap) {
        return createFilterQuery(queryMap, "01");
    }

    private String createFilterQuery(Map<LogicExpression, LogicOperator> queryMap, String distributionChannel) {
        LogicExpression distributionChannelExpression = LogicExpression.builder().field(MaterialField.DistributionChannel).value(distributionChannel).comparator(LogicComparator.Equal).build();
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
