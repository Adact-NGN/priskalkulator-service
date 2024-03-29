package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.web.dto.sap.CustomerDTO;
import no.ding.pk.web.enums.SapCustomerField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.StringContains.containsString;

@Disabled("Need to isolate the scope")
@Tag("integrationtest")
@Profile("itest")
@ActiveProfiles("itest")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerServiceTest extends AbstractIntegrationConfig {

    private final String url = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_hp_customers2/srvd_a2x/sap/zapi_hp_customers/0001/Kunder";
    // private String url = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZAPI_ECOM_CUSTOMERS_V2/ZC_ECOM_CUSTOMER";

    private CustomerServiceImpl service;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        service = new CustomerServiceImpl("sapUsername", "sapPassword", url, new ObjectMapper());
    }

    @Test
    void testFetchCustomers() throws IOException, URISyntaxException, InterruptedException {

        List<String> expansionFields = new ArrayList<>();
        expansionFields.add("KontaktPersoner");
        List<CustomerDTO> actual = service.fetchCustomersJSON("100", "", expansionFields, null);

        assertThat(actual, not(empty()));
    }

    @Test
    void shouldFetchTheNextBatchOfCustomerObjects() {
        List<String> expansionFields = new ArrayList<>();
        expansionFields.add("KontaktPersoner");
        List<CustomerDTO> firstBatch = service.fetchCustomersJSON("100", "", expansionFields, null);
        List<CustomerDTO> secondBatch = service.fetchCustomersJSON("100", "", expansionFields, 100);

        CustomerDTO[] firstBatchArray = new CustomerDTO[firstBatch.size()];
        firstBatch.toArray(firstBatchArray);

        assertThat(firstBatchArray, not(org.hamcrest.collection.ArrayMatching.arrayContainingInAnyOrder(secondBatch)));
    }

    @Test
    void shouldFetchCustomerByCustomerNumber() {
        String knr = "126094";
        List<CustomerDTO> actual = service.findCustomerByCustomerNumber(knr);

        assertThat(actual, not(empty()));
        assertThat(actual.get(0).getCustomerNumber(), equalTo(knr));
    }

    @Test
    void shouldSearchForCustomerByName() {
        String partialName = "Notodd";
        List<CustomerDTO> actual = service.searchCustomerBy("100", SapCustomerField.Navn1.getValue(), partialName);

        assertThat(actual, not(empty()));
        assertThat(actual.get(0).getName1(), containsString(partialName));
    }


}
