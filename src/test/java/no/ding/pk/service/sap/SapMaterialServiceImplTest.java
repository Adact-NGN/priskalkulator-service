package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.utils.RequestHeaderUtil;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.converters.SapMaterialDTODeserializer;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled("SAP is down")
class SapMaterialServiceImplTest {

    private SapMaterialService sapMaterialService;

    private final SapHttpClient sapHttpClient = mock(SapHttpClient.class);

    @BeforeEach
    public void setup() {

        SimpleModule module = new SimpleModule();
        module.addDeserializer(MaterialDTO.class, new SapMaterialDTODeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        // Setting objectMapper for util class.
        LocalJSONUtils localJSONUtils = new LocalJSONUtils(objectMapper);
        sapMaterialService = new SapMaterialServiceImpl("rubbish", sapHttpClient, localJSONUtils);
    }

    @Test
    public void shouldGetMaterialFromSapAndDeserializeIt() throws IOException {
        HttpRequest httpRequest = getRequest();
        when(sapHttpClient.createGetRequest(anyString(), any())).thenReturn(httpRequest);
        HttpResponse<String> response = getResponse();
        when(sapHttpClient.getResponse(any())).thenReturn(response);
        MaterialDTO materialDTO = sapMaterialService.getMaterialByMaterialNumberAndSalesOrg("119901", "100");
        assertThat(materialDTO.getMaterial(), equalTo("119901"));
    }

    private HttpRequest getRequest() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        UriComponents url = UriComponentsBuilder
                .fromUriString("https://rubbish.no")
                .queryParams(params)
                .build();

        return HttpRequest.newBuilder()
                .GET()
                .uri(url.toUri())
                .header(org.springframework.http.HttpHeaders.AUTHORIZATION, RequestHeaderUtil.getBasicAuthenticationHeader("sapUsername", "sapPassword"))
                .build();
    }

    private static HttpResponse<String> getResponse() throws IOException {
        ClassLoader classLoader = SapMaterialServiceImplTest.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Material119901.json")).getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return json;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return URI.create("rubbish");
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }
}