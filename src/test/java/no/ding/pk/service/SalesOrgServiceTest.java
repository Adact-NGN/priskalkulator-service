package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import no.ding.pk.web.dto.sap.SalesOrgDTO;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;


@Disabled
@Tag("integrationtest")
@Profile("itest")
@ActiveProfiles("itest")
@SpringBootTest
public class SalesOrgServiceTest {

    private String url = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_ecom_salesorg_postal/srvd_a2x/sap/zapi_ecom_salesorg_postal/0001/SalesorgPostal";

    private SalesOrgServiceImpl service = new SalesOrgServiceImpl(url, new ObjectMapper());
    
    @Test
    void shouldGetAllSalesOrgFromSAP() {
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
