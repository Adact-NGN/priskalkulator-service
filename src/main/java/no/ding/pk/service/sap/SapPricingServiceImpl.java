package no.ding.pk.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.pricing.*;
import no.ding.pk.web.handlers.ErrorRetrievingTokenException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class SapPricingServiceImpl implements SapPricingService {
    private static final Logger log = LoggerFactory.getLogger(SapPricingServiceImpl.class);

    private final PriceOfferService priceOfferService;
    private final ObjectWriter objectWriter;

    private final String sapPricingConditionRecordUrl;
    private final SapHttpClient sapHttpClient;
    private static final String cCsrfTokenHeaderName = "x-csrf-token";

    public SapPricingServiceImpl(PriceOfferService priceOfferService,
                                 ObjectMapper objectMapper,
                                 @Value("${PK_SAP_API_PRICING_URL}") String sapPricingConditionRecordUrl,
                                 SapHttpClient sapHttpClient) {
        this.priceOfferService = priceOfferService;
        this.objectWriter = objectMapper.writer();
        this.sapPricingConditionRecordUrl = sapPricingConditionRecordUrl;
        this.sapHttpClient = sapHttpClient;
    }

    /**
     * Add new pricing entity to SAP
     * <a href="https://sapdev.norskgjenvinning.no/sap(bD1ubyZjPTEwMA==)/bc/bsp/sap/zgw_openapi/index.html?service=API_SLSPRICINGCONDITIONRECORD_SRV&version=0001&repository=&group=#/Betingelsesposter/post_A_SlsPrcgConditionRecord">Add new entity to A_SlsPrcgConditionRecord</a>
     *
     * @return List of ConditionRecordValidityDTO objects
     */
    @Override
    public List<SapCreatePricingEntitiesResponse> updateMaterialPriceEntities(Long priceOfferId, String customerNumber, String nodeNumber, String customerName,
                                                                              List<PricingEntityCombinationMap> pricingEntityCombinationMaps) {
        Optional<PriceOffer> priceOfferOptional = priceOfferService.findById(priceOfferId);

        if(priceOfferOptional.isEmpty()) {
            String errorMessage = String.format("No price offer with id %d, found.", priceOfferId);
            log.debug(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        PriceOffer priceOffer = priceOfferOptional.get();

        if(StringUtils.isNotBlank(customerNumber) && !StringUtils.equals(customerNumber, priceOffer.getCustomerNumber())) {
            priceOfferService.updateCustomerNumber(priceOffer.getId(), customerNumber);
        }

        Map<String, PriceRow> materialPriceRowMap = createMaterialPriceRowMap(priceOffer);

        List<ConditionRecordDTO> conditionRecordDTOS = createConditionRecordDTOS(pricingEntityCombinationMaps, materialPriceRowMap, priceOffer, nodeNumber);

        Map<String, String> tokenAndSessionMap = getTokenAndSession();

        Map<String, String> headers = addStandardHeaders(tokenAndSessionMap);

        List<SapCreatePricingEntitiesResponse> materialPriceUpdatedStatusList = new ArrayList<>();

        for (ConditionRecordDTO conditionRecordDTO : conditionRecordDTOS) {
            String requestBody = writeObjectToRequestBodyString(conditionRecordDTO);

            HttpRequest request = sapHttpClient.createPostRequest(sapPricingConditionRecordUrl, requestBody, new LinkedMultiValueMap<>(), headers);

            log.debug("Sending request with body:\n{}", requestBody);

            HttpResponse<String> response = sapHttpClient.getResponse(request);

            if(response.statusCode() == HttpStatus.FORBIDDEN.value()) {
                throw new RuntimeException("Request was not authorized by server, see message: " + response.body());
            }

            if(response.statusCode() != HttpStatus.CREATED.value()) {
                log.debug("Error adding new pricing entity. Service returned with status: {}, with message: {}", response.statusCode(), response.body());
                log.debug(response.headers().toString());
                SapCreatePricingEntitiesResponse responseStatus = createUpdatedStatusResponse(conditionRecordDTO, false);
                materialPriceUpdatedStatusList.add(responseStatus);
            } else {
                SapCreatePricingEntitiesResponse responseStatus = createUpdatedStatusResponse(conditionRecordDTO, true);
                materialPriceUpdatedStatusList.add(responseStatus);
            }
        }

        return materialPriceUpdatedStatusList;
    }

    private static List<ConditionRecordDTO> createConditionRecordDTOS(List<PricingEntityCombinationMap> pricingEntityCombinationMaps,
                                                                      Map<String, PriceRow> materialPriceRowMap,
                                                                      PriceOffer priceOffer,
                                                                      String nodeNumber) {
        List<ConditionRecordDTO> conditionRecordDTOS = new ArrayList<>();
        for (PricingEntityCombinationMap combinationMap : pricingEntityCombinationMaps) {

            String lookupKey = combinationMap.getMaterialId();
            PriceRow priceRow = materialPriceRowMap.get(lookupKey);

            if(priceRow == null) {
                log.debug("Could not find price row with material lookup key: {}", lookupKey);
                continue;
            }

            ConditionRecordDTO conditionRecordDTO = ConditionRecordDTO.builder(
                            combinationMap.getKeyCombinationTableName(),
                            combinationMap.getConditionCode(),
                            priceRow.getDiscountLevelPrice(),
                            combinationMap.getValueUnit(),
                            priceRow.getMaterial().getScaleQuantum(),
                            priceRow.getMaterial().getQuantumUnit())
                    .build();

            ConditionRecordValidityItemDTO.ConditionRecordValidityItemDTOBuilder validityItemDTOBuilder = ConditionRecordValidityItemDTO.builder(
                            combinationMap.getSalesOrg(),
                            combinationMap.getSalesOffice(),
                            combinationMap.getMaterialNumber());

            if(StringUtils.isNotBlank(nodeNumber)) {
                validityItemDTOBuilder.customerHierarchy(nodeNumber);
            } else {
                validityItemDTOBuilder.customer(priceOffer.getCustomerNumber());
            }

            List<ConditionRecordValidityItemDTO> conditionRecordValidityItems = new ArrayList<>();
            conditionRecordValidityItems.add(validityItemDTOBuilder.build());

            conditionRecordDTO.setConditionRecordValidity(ConditionRecordValidityDTO.builder()
                            .conditionRecordValidityItemDTOS(conditionRecordValidityItems)
                    .build());

            conditionRecordDTOS.add(conditionRecordDTO);
        }
        return conditionRecordDTOS;
    }

    private static SapCreatePricingEntitiesResponse createUpdatedStatusResponse(ConditionRecordDTO conditionRecordDTO, boolean isUpdated) {
        ConditionRecordValidityItemDTO validityDTO = conditionRecordDTO.getConditionRecordValidity().getConditionRecordValidityItemDTOS().get(0);
        return new SapCreatePricingEntitiesResponse(
                validityDTO.getSalesOrganization(),
                validityDTO.getSalesOffice(),
                validityDTO.getCustomer(),
                validityDTO.getCustomerHierarchy(),
                validityDTO.getMaterial(),
                validityDTO.getDeviceType(),
                validityDTO.getZone(),
                isUpdated);
    }

    private String writeObjectToRequestBodyString(ConditionRecordDTO conditionRecordDTO) {
        try {
            return objectWriter.writeValueAsString(conditionRecordDTO);
        } catch (JsonProcessingException e) {
            log.debug("Error processing JSON when writing object to JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> addStandardHeaders(Map<String, String> token) {
        Map<String, String> headerMap = new HashMap<>(token);
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return headerMap;
    }

    private Map<String, PriceRow> createMaterialPriceRowMap(PriceOffer priceOffer) {

        Map<String, PriceRow> materialPriceRowMap = new HashMap<>();

        for (SalesOffice salesOffice : priceOffer.getSalesOfficeList()) {

            for (PriceRow priceRow : salesOffice.getMaterialList()) {
                String sb = salesOffice.getSalesOrg() + "_"
                        + salesOffice.getSalesOffice() + "_"
                        + String.join("_", priceRow.getMaterialId().values());
                materialPriceRowMap.put(sb, priceRow);
            }
        }

        return materialPriceRowMap;
    }

    private Map<String, String> getTokenAndSession() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$top", "1");

        Map<String, String> headers = Map.of(cCsrfTokenHeaderName, "fetch");
        HttpRequest request = sapHttpClient.createGetRequest(sapPricingConditionRecordUrl, params, headers);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            log.debug("Getting token and cookie request successful");

            Optional<String> optionalToken = response.headers().firstValue(cCsrfTokenHeaderName);
            List<String> optionalCookie = response.headers().allValues("set-cookie");

            if(optionalToken.isPresent() && !optionalCookie.isEmpty()) {
                String mysapsso2 = getCookiePart(optionalCookie, "MYSAPSSO2");
                String sapSession = getCookiePart(optionalCookie, "SAP_SESSIONID");
                String userContext = getCookiePart(optionalCookie, "sap-usercontext");

                return Map.of(cCsrfTokenHeaderName, optionalToken.get(), HttpHeaders.COOKIE, String.join("", String.join("; ", mysapsso2, sapSession, userContext)));
            }
        }

        log.debug("Requesting for Token resulting in bad response: {}", response.statusCode());

        throw new ErrorRetrievingTokenException();
    }

    private static String getCookiePart(List<String> optionalCookie, String cookiePartPrefix) {
        Optional<String> cookiePartOptional = optionalCookie.stream().filter(s -> s.startsWith(cookiePartPrefix)).findFirst();

        String cookiePart;
        if(cookiePartOptional.isPresent()) {
            cookiePart = cookiePartOptional.get();

            if(cookiePart.split(";").length > 0) {
                cookiePart = cookiePart.split(";")[0];
            }
        } else {
            throw new RuntimeException(String.format("Could not get %s cookie element", cookiePartPrefix));
        }
        return cookiePart;
    }
}
