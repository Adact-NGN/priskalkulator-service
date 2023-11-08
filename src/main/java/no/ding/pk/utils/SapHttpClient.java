package no.ding.pk.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

@Component
public class SapHttpClient {

    private final static Logger log = LoggerFactory.getLogger(SapHttpClient.class);

    private final String sapUsername;

    private final String sapPassword;

    public SapHttpClient(@Value(value = "${sap.username}") String sapUsername, @Value(value = "${sap.password}") String sapPassword) {
        this.sapUsername = sapUsername;
        this.sapPassword = sapPassword;
    }

    public HttpRequest createGetRequest(String urlString, MultiValueMap<String, String> params) {
        return createGetRequest(urlString, params, null);
    }

    public HttpRequest createGetRequest(String urlString, MultiValueMap<String, String> params, Map<String, String> headers) {
        if(StringUtils.isBlank(sapUsername) || StringUtils.isBlank(sapPassword)) {
            log.debug("Credentials for SAP service is empty");
            throw new RuntimeException("Credentials for SAP service is empty");
        }

        if(!UrlValidator.getInstance().isValid(urlString)) {
            log.debug("Cannot execute GET request, malformed URL: {}", urlString);
            throw new RuntimeException(String.format("Cannot execute GET request, malformed URL: %s", urlString));
        }

        UriComponents url = getUriComponents(urlString, params);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(url.toUri())
                .setHeader(HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword));

        addHeaders(headers, requestBuilder);

        return requestBuilder
                .build();
    }

    public HttpRequest createPostRequest(String sapPricingConditionRecordUrl, String body, MultiValueMap<String, String> params, Map<String, String> headers) {

        HttpRequest.BodyPublisher bodyPublisher = StringUtils.isBlank(body) ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);

        UriComponents url = getUriComponents(sapPricingConditionRecordUrl, params);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(url.toUri())
                .setHeader(HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword));

        addHeaders(headers, requestBuilder);

        return requestBuilder.build();
    }

    private static UriComponents getUriComponents(String urlString, MultiValueMap<String, String> params) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder
                .fromUriString(urlString);

        if(params != null) {
            urlBuilder.queryParams(params);
        }

        return urlBuilder.build();
    }

    private static void addHeaders(Map<String, String> headers, HttpRequest.Builder requestBuilder) {
        if(headers != null) {
            for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                requestBuilder.setHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
    }

    public HttpResponse<String> getResponse(HttpRequest request) {
        HttpClient client = HttpClient.newBuilder()
        .build();

        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {


            throw new Error(e.getMessage());
        }
    }
}
