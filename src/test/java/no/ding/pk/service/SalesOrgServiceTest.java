package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.junit.jupiter.api.Test;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.web.dto.SalesOrgDTO;

@Tag("integrationtest")
@Profile("itest")
@ActiveProfiles("itest")
@SpringBootTest
public class SalesOrgServiceTest {

    private String url = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_ecom_salesorg_postal/srvd_a2x/sap/zapi_ecom_salesorg_postal/0001/SalesorgPostal";

    private SalesOrgServiceImpl service = new SalesOrgServiceImpl("AZURE_ECOM", "AzureEcom@NGN2022", url, new ObjectMapper());
    
    @Test
    void shouldGetAllSalesOrgFromSAP() {
        List<SalesOrgDTO> result = service.getAll();

        assertThat(result, not(empty()));
    }

    @Test
    void shouldGetAllSaleOrgBasedOnQuery() {
        String query = "PostalNumber eq '0178' and City eq 'OSLO'";
        List<SalesOrgDTO> result = service.findByQuery(query);

        assertThat(result, not(empty()));
    }
}
