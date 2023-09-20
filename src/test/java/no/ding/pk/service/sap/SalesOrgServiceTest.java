package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.ObjectMapperConfig;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.SalesOrgDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.net.http.HttpResponse;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled("Tests against SAP should not be automated.")
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
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(httpResponse.body()).thenReturn("{\n" +
                "    \"@odata.context\": \"$metadata#SalesorgPostal\",\n" +
                "    \"@odata.metadataEtag\": \"W/\\\"20230828115643\\\"\",\n" +
                "    \"value\": [\n" +
                "        {\n" +
                "            \"SalesOrganization\": \"100\",\n" +
                "            \"PostalCode\": \"3933\",\n" +
                "            \"SalesOffice\": \"104\",\n" +
                "            \"SalesOfficeName\": \"Skien\",\n" +
                "            \"SalesZone\": \"0000000002\",\n" +
                "            \"City\": \"PORSGRUNN\"\n" +
                "        }\n" +
                "    ]\n" +
                "}");
        when(sapHttpClient.getResponse(any())).thenReturn(httpResponse);
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
