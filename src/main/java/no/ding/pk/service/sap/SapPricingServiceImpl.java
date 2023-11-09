package no.ding.pk.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.pricing.ConditionRecordDTO;
import no.ding.pk.web.dto.sap.pricing.ConditionRecordValidityDTO;
import no.ding.pk.web.dto.sap.pricing.PricingEntityCombinationMap;
import no.ding.pk.web.dto.sap.pricing.SapCreatePricingEntitiesResponse;
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
    public List<SapCreatePricingEntitiesResponse> updateMaterialPriceEntities(Long priceOfferId, String customerNumber, String customerName,
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

        List<ConditionRecordDTO> conditionRecordDTOS = createConditionRecordDTOS(pricingEntityCombinationMaps, materialPriceRowMap, priceOffer);

        String token = getToken();

        Map<String, String> headers = getStandardHeaders(token);

        List<SapCreatePricingEntitiesResponse> materialPriceUpdatedStatusList = new ArrayList<>();

        for (ConditionRecordDTO conditionRecordDTO : conditionRecordDTOS) {
            String requestBody = writeObjectToRequestBodyString(conditionRecordDTO);

            HttpRequest request = sapHttpClient.createPostRequest(sapPricingConditionRecordUrl, requestBody, new LinkedMultiValueMap<>(), headers);

            HttpResponse<String> response = sapHttpClient.getResponse(request);

            if(response.statusCode() != HttpStatus.CREATED.value()) {
                log.debug("Error adding new pricing entity. Service returned with status: " + response.statusCode());
                SapCreatePricingEntitiesResponse responseStatus = createUpdatedStatusResponse(conditionRecordDTO, false);
                materialPriceUpdatedStatusList.add(responseStatus);
            } else {
                SapCreatePricingEntitiesResponse responseStatus = createUpdatedStatusResponse(conditionRecordDTO, true);
                materialPriceUpdatedStatusList.add(responseStatus);
            }
        }

        return materialPriceUpdatedStatusList;
    }

    private static List<ConditionRecordDTO> createConditionRecordDTOS(List<PricingEntityCombinationMap> pricingEntityCombinationMaps, Map<String, PriceRow> materialPriceRowMap, PriceOffer priceOffer) {
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

            ConditionRecordValidityDTO validityDTO = ConditionRecordValidityDTO.builder(
                            combinationMap.getSalesOrg(),
                            combinationMap.getSalesOffice(),
                            priceOffer.getCustomerNumber(),
                            combinationMap.getMaterialNumber())
                    .build();

            conditionRecordDTO.addConditionRecordValidity(validityDTO);

            conditionRecordDTOS.add(conditionRecordDTO);
        }
        return conditionRecordDTOS;
    }

    private static SapCreatePricingEntitiesResponse createUpdatedStatusResponse(ConditionRecordDTO conditionRecordDTO, boolean isUpdated) {
        ConditionRecordValidityDTO validityDTO = conditionRecordDTO.getConditionRecordValidityList().get(0);
        return new SapCreatePricingEntitiesResponse(
                validityDTO.getSalesOrganization(),
                validityDTO.getSalesOffice(),
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

    private static Map<String, String> getStandardHeaders(String token) {
        return Map.of(cCsrfTokenHeaderName, token,
                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
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

    private String getToken() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$top", "1");

        Map<String, String> headers = Map.of(cCsrfTokenHeaderName, "fetch");
        HttpRequest request = sapHttpClient.createGetRequest(sapPricingConditionRecordUrl, params, headers);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            log.debug("Got token request successful");

            Optional<String> optionalToken = response.headers().firstValue(cCsrfTokenHeaderName);

            if(optionalToken.isPresent()) {
                return optionalToken.get();
            }
        }

        log.debug("Requesting for Token resulting in bad response: {}", response.statusCode());

        throw new ErrorRetrievingTokenException();
    }
}
