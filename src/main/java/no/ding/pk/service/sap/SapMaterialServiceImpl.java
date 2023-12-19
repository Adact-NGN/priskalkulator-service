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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SapMaterialServiceImpl implements SapMaterialService {
    private static final Logger log = LoggerFactory.getLogger(SapMaterialServiceImpl.class);

    private final String materialServiceUrl;
    private final SapHttpClient sapHttpClient;

    private final LocalJSONUtils localJSONUtils;
    private final String DISTRIBUTION_CHANNEL_DOWN_STREAM = "01";

    @Autowired
    public SapMaterialServiceImpl(@Value(value = "${PK_SAP_API_MATERIAL_URL}") String materialServiceUrl,
                                  SapHttpClient sapHttpClient,
                                  LocalJSONUtils localJSONUtils) {
        this.materialServiceUrl = materialServiceUrl;
        this.sapHttpClient = sapHttpClient;
        this.localJSONUtils = localJSONUtils;
    }

    @Cacheable(cacheNames = "sapMaterialCache", key = "{#salesOrg + '_' + #material}")
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

        if (response.statusCode() == HttpStatus.OK.value() && StringUtils.contains(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {

            List<MaterialDTO> materialDTOS = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class);

            if(materialDTOS.isEmpty()) {
                throw new RuntimeException("SAP could not find any materials with material number: " + material);
            }

            return materialDTOS.get(0);
        } else {
            log.debug("Response code was {}, but expected content type did not match. Expected content type is {}, got {}", response.statusCode(), MediaType.APPLICATION_JSON_VALUE, headerContentType);
        }

        return null;
    }

    @Cacheable(cacheNames = "sapMaterialCache", key = "#salesOrg")
    @Override
    public List<MaterialDTO> getAllMaterialsForSalesOrgBy(String salesOrg, Integer page, Integer pageSize) {
        log.debug("Material cache not hit");

        LogicExpression salesOrgExpression = LogicExpression.builder().field(MaterialField.SalesOrganization).value(salesOrg).comparator(LogicComparator.Equal).build();
        String filterQuery = createFilterQuery(Maps.newLinkedHashMap(ImmutableMap.of(salesOrgExpression, LogicOperator.And)));

        MultiValueMap<String, String> params = createParameterMap(filterQuery, page, pageSize, "json");

        HttpRequest request = sapHttpClient.createGetRequest(materialServiceUrl, params);

        log.debug("Created request: " + request.toString());

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        String headerContentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null);

        if (response.statusCode() == HttpStatus.OK.value() && StringUtils.contains(headerContentType, MediaType.APPLICATION_JSON_VALUE)) {
            List<MaterialDTO> materialDTOList = localJSONUtils.jsonToObjects(response.body(), MaterialDTO.class); //jsonToMaterialDTO(response);

            return materialDTOList;
        } else {
            log.debug("Response code was {}, but expected content type did not match. Expected content type is {}, got {}", response.statusCode(), MediaType.APPLICATION_JSON_VALUE, headerContentType);
        }

        return new ArrayList<>();
    }

    private static MultiValueMap<String, String> createParameterMap(String filterQuery, Integer page, Integer pageSize, String format) {
        MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        if(StringUtils.isNotBlank(format)) {
            params.add("$format", format);
        }

        if(pageSize != null) {
            params.add("$top", String.valueOf(pageSize));
        }

        if(page != null && pageSize != null) {
            Integer skipTokens = page * pageSize;

            params.add("$skiptoken", String.valueOf(skipTokens));
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
