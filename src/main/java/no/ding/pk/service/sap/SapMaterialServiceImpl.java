package no.ding.pk.service.sap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.utils.LogicExpression;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SapMaterialServiceImpl implements SapMaterialService {
    private static final Logger log = LoggerFactory.getLogger(SapMaterialServiceImpl.class);

    private final String materialServiceUrl;
    private final SapHttpClient sapHttpClient;

    // private StandardPriceService standardPriceService;
    private final InMemory3DCache<String, String, MaterialDTO> inMemoryCache;

    private LocalJSONUtils localJSONUtils;

    @Autowired
    public SapMaterialServiceImpl(@Value(value = "${sap.api.material.url}") String materialServiceUrl,
                                  SapHttpClient sapHttpClient,
                                LocalJSONUtils localJSONUtils,
                                  @Qualifier("materialInMemoryCache") InMemory3DCache<String, String, MaterialDTO> inMemoryCache) {
        this.materialServiceUrl = materialServiceUrl;
        this.sapHttpClient = sapHttpClient;
        this.localJSONUtils = localJSONUtils;
        this.inMemoryCache = inMemoryCache;
    }

    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrg(String material, String salesOrg) {
        if(inMemoryCache.size(salesOrg) == 0 || inMemoryCache.isExpired()) {
            initiateCacheBuild(salesOrg, material);
        }

        if(!inMemoryCache.contains(salesOrg, material)) {
            updateMaterialCache(salesOrg, null, material, null, null);
        }

        return inMemoryCache.get(salesOrg, material);
    }

    @Override
    public MaterialDTO getMaterialByMaterialNumberAndSalesOrgAndSalesOffice(String material, String zone, String salesOrg,
                                                                                  String salesOffice) {

        LogicExpression materialExpression = LogicExpression.builder().field(MaterialField.Material).value(material).comparator(LogicComparator.Equal).build();
        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        LogicExpression salesOfficeExpression = LogicExpression.builder().field(MaterialField.SalesOffice).value(salesOffice).comparator(LogicComparator.Equal).build();

        Map<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(
                ImmutableMap.of(materialExpression, LogicOperator.And,
                        salesOrgExpression, LogicOperator.And));
        String filterQuery = createFilterQuery(queryMap);

        log.debug("Got material {} sales org {} sales office {}", material, salesOrg, salesOffice);
        if(inMemoryCache.size(salesOrg) == 0 || inMemoryCache.isExpired()) {
            log.debug("Memory cache is empty or expired, refreshing cache!");
            initiateCacheBuild(salesOrg, material);

            log.debug("Update Material with standard price: material {} sales org {} sales office {}", material, salesOffice, salesOrg);

            updateMaterialCache(salesOrg, salesOffice, filterQuery);
        }

        if(!inMemoryCache.contains(salesOrg, material)) {
            log.debug("Cache is missing the material.");

            updateMaterialCache(salesOrg, salesOffice, filterQuery);
        }

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
    public List<MaterialDTO> getAllMaterialsForSalesOrg(String salesOrg, Integer page, Integer pageSize) {
        // Get material size count from SAP
        Integer materialCount = getCountFromSap(salesOrg, null);
        log.debug("Got amount of materials for sales org: {} amount: {} vs cache {}", salesOrg, materialCount, inMemoryCache.size(salesOrg));


        // if(inMemoryCache.size(salesOrg) == 0 || inMemoryCache.isExpired() || inMemoryCache.size(salesOrg) < materialCount) {
            LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
            String filterQuery = createFilterQuery(Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And)));

            MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);
            log.debug("MaterialDTOList {}", materialDTOList.size());
            return materialDTOList;
        }

        //     log.debug(String.format("Filter query: %s", filterQuery));

        //     buildMaterialCache(salesOrg, filterQuery, page, pageSize);

        //     log.debug("Returning from new cache");
        // }

        return inMemoryCache.getAllInList(salesOrg);
    }

    private Integer getCountFromSap(String salesOrg, String zone) {
        String countUrl = String.format("%s/%s", materialServiceUrl, "$count");

        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        LinkedHashMap<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And));

        includeOrExcludeZonedPricedMaterials(zone, queryMap);

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

    private static void includeOrExcludeZonedPricedMaterials(String zone, Map<LogicExpression, LogicOperator> queryMap) {
        if(StringUtils.isNotBlank(zone)) {
            LogicExpression zoneDifferentiated = LogicExpression.builder().field(MaterialField.SubCategoryDescription).value("Sone differensiert").comparator(LogicComparator.Equal).build();
            queryMap.put(zoneDifferentiated, LogicOperator.And);
        } else {
            LogicExpression zoneDifferentiated = LogicExpression.builder().field(MaterialField.SubCategoryDescription).value("Sone differensiert").comparator(LogicComparator.NotEqual).build();
            queryMap.put(zoneDifferentiated, LogicOperator.And);
        }
    }

    @Override
    public List<MaterialDTO> getAllMaterialsForSalesOrgAndSalesOffice(String salesOrg, String salesOffice, String zone, Integer page, Integer pageSize) {
        Integer materialCount = getCountFromSap(salesOrg, zone);
        log.debug("Got amount of materials for sales org: {} amount: {} vs cache {}", salesOrg, materialCount, inMemoryCache.size(salesOrg));
        if(inMemoryCache.size(salesOrg) == 0 || inMemoryCache.isExpired() || inMemoryCache.size(salesOrg) < materialCount) {
            HashMap<LogicExpression, LogicOperator> queryMap = Maps.newLinkedHashMap(ImmutableMap.of(LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build(), LogicOperator.And));

            includeOrExcludeZonedPricedMaterials(zone, queryMap);

            String filterQuery = createFilterQuery(queryMap);

            log.debug("Filter query: {}", filterQuery);

            buildMaterialCache(salesOrg, salesOffice, filterQuery, page, pageSize);

            log.debug("Returning from new cache");
        }

        return inMemoryCache.getAllInList(salesOrg);
    }

    private void buildMaterialCache(String salesOrg, String filterQuery, Integer page, Integer pageSize) {
        log.debug("Building cache");

        updateMaterialCache(salesOrg, null, filterQuery, page, pageSize);
    }

    private void buildMaterialCache(String salesOrg, String salesOffice, String filterQuery, Integer page, Integer pageSize) {
        log.debug("Building cache");

        updateMaterialCache(salesOrg, salesOffice, filterQuery, page, pageSize);
    }

    private void updateMaterialCache(String salesOrg, String salesOffice, String filterQuery) {
        log.debug("Got material {} sales org {} sales office {}", salesOrg, salesOffice);

        updateMaterialCache(salesOrg, salesOffice, filterQuery, null, null);
    }

    private void updateMaterialCache(String salesOrg, String salesOffice, String filterQuery, Integer page, Integer pageSize) {
        log.debug("Update material cache with sales org {} sales office {}", salesOrg, salesOffice);

        MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);

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
        LogicExpression categoryIdExpression = LogicExpression.builder().field(MaterialField.CategoryId).value("").comparator(LogicComparator.NotEqual).build();
        LogicExpression distributionChannelExpression = LogicExpression.builder().field(MaterialField.DistributionChannel).value(distributionChannel).comparator(LogicComparator.Equal).build();
        queryMap.put(categoryIdExpression, LogicOperator.And);
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
