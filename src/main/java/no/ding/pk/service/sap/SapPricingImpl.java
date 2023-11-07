package no.ding.pk.service.sap;

import no.ding.pk.utils.RequestHeaderUtil;
import no.ding.pk.utils.SapHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpRequest;

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

    private String getToken() {
        String tokenPath = "/sap/opu/odata/sap/API_SLSPRICINGCONDITIONRECORD_SRV/A_SlsPrcgConditionRecord?$top=1";

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(sapPricingConditionRecordUrl).queryParam("$top", "1");
        HttpRequest getTokenRequest = HttpRequest.newBuilder()
                .GET()
                .headers("X-CSRF-Token", "fetch", HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword)))
    }
}
