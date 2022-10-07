package no.ding.pk.utils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;

@Component
public class HttpRequestResponseUtil {

    private static String sapUsername;
    private static String sapPassword;

    @Autowired
    public HttpRequestResponseUtil(
    @Value(value = "${sap.username}") String username,
    @Value(value = "${sap.password}") String password) {
        HttpRequestResponseUtil.sapUsername = username;
        HttpRequestResponseUtil.sapPassword = password;
    }

    public static HttpRequest createGetRequest(String urlString, MultiValueMap<String, String> params) {
        UriComponents url = UriComponentsBuilder
        .fromUriString(urlString)
        .queryParams(params)
        .build();

        return HttpRequest.newBuilder()
        .GET()
        .uri(url.toUri())
        .header(HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword))
        .build();
    }

    public static HttpResponse<String> getResponse(HttpRequest request) {
        HttpClient client = HttpClient.newBuilder()
        .build();

        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Error(e.getMessage());
        }
    }
}
