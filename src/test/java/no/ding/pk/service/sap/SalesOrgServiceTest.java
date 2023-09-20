package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.ObjectMapperConfig;
import no.ding.pk.service.sap.SalesOrgServiceImpl;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.SalesOrgDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("integrationtest")
@ActiveProfiles("itest")
@Import({ObjectMapperConfig.class})
public class SalesOrgServiceTest extends AbstractIntegrationConfig {

    private String url = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_ecom_salesorg_postal/srvd_a2x/sap/zapi_ecom_salesorg_postal/0001/SalesorgPostal";

    @MockBean
    private SapHttpClient sapHttpClient;

    @Autowired
    private ObjectMapper objectMapper;

    private SalesOrgServiceImpl service;

    @BeforeEach
    public void setup() {
        service = new SalesOrgServiceImpl(url, objectMapper, sapHttpClient);
    }

    @Test
    void shouldGetAllSalesOrgFromSAP() {
        when(sapHttpClient.getResponse(any())).thenReturn(new HttpResponse() {
            @Override
            public int statusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public Object body() {
                return null;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        });
        List<SalesOrgDTO> result = service.getAll();

        assertThat(result, not(empty()));
    }

    @Test
    void shouldGetAllSaleOrgBasedOnQuery() {
        String query = "PostalNumber eq '0178' and City eq 'OSLO'";
        List<SalesOrgDTO> result = service.findByQuery(query, null);

        assertThat(result, not(empty()));
    }

    @Test
    void shouldGetAllSaleOrgBasedOnQueryPostalOrganization() {
        String query = "SalesOrganization eq '100'";
        List<SalesOrgDTO> result = service.findByQuery(query, null);

        assertThat(result, not(empty()));
    }
}
