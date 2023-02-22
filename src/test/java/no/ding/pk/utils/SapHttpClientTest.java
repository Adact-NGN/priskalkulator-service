package no.ding.pk.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;


class SapHttpClientTest {

    private final SapHttpClient sapHttpClient = new SapHttpClient("rubbish", "rubbish");

    @Test
    public void shouldCreateGetRequest() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$format", "json");
        HttpRequest getRequest = sapHttpClient.createGetRequest("https://testurl.no", params);

        assertThat(getRequest, notNullValue());
    }
}