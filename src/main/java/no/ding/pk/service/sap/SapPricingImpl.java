package no.ding.pk.service.sap;

import no.ding.pk.utils.RequestHeaderUtil;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.pricing.ConditionRecordValidityDTO;
import no.ding.pk.web.handlers.ErrorRetrievingTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@Service
public class SapPricingImpl implements SapPricing {
    private static final Logger log = LoggerFactory.getLogger(SapPricingImpl.class);

    private final String sapPricingConditionRecordUrl;
    private final SapHttpClient sapHttpClient;

    public SapPricingImpl(@Value("${PK_SAP_API_PRICING_URL}") String sapPricingConditionRecordUrl,
                          SapHttpClient sapHttpClient) {
        this.sapPricingConditionRecordUrl = sapPricingConditionRecordUrl;
        this.sapHttpClient = sapHttpClient;
    }

    /**
     * Add new pricing entity to SAP
     * <a href="https://sapdev.norskgjenvinning.no/sap(bD1ubyZjPTEwMA==)/bc/bsp/sap/zgw_openapi/index.html?service=API_SLSPRICINGCONDITIONRECORD_SRV&version=0001&repository=&group=#/Betingelsesposter/post_A_SlsPrcgConditionRecord">Add new entity to A_SlsPrcgConditionRecord</a>
     */
    public ConditionRecordValidityDTO updateMaterialPricingEntity() {
        String token = getToken();

        Map<String, String> headers = Map.of("X-CSRF-Token", token);
        HttpRequest request = sapHttpClient.createPostRequest(sapPricingConditionRecordUrl, new LinkedMultiValueMap<>(), headers);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
                
        }

        throw new RuntimeException("Error adding new pricing entity. Service returned with status: " + response.statusCode());
    }

    private String getToken() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$top", "1");

        Map<String, String> headers = Map.of("X-CSRF-Token", "fetch");
        HttpRequest request = sapHttpClient.createGetRequest(sapPricingConditionRecordUrl, params, headers);

        HttpResponse<String> response = sapHttpClient.getResponse(request);

        if(response.statusCode() == HttpStatus.OK.value()) {
            log.debug("Got token request successful");

            Optional<String> optionalToken = response.headers().firstValue("x-csrf-token");

            if(optionalToken.isPresent()) {
                return optionalToken.get();
            }
        }

        log.debug("Requesting for Token resulting in bad response: %d", response.statusCode());

        throw new ErrorRetrievingTokenException();
    }
}
