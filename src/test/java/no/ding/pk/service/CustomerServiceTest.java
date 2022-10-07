package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.web.dto.CustomerDTO;

public class CustomerServiceTest {

    private String url = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_hp_customers2/srvd_a2x/sap/zapi_hp_customers/0001/Kunder";

    private CustomerServiceImpl service = new CustomerServiceImpl("AZURE_ECOM", "AzureEcom@NGN2022", url, new ObjectMapper());

    @Test
    void testFetchCustomers() throws IOException, URISyntaxException, InterruptedException {

        List<String> expansionFields = new ArrayList<>();
        expansionFields.add("KontaktPersoner");
        List<CustomerDTO> actual = service.fetchCustomersJSON("", "Node", expansionFields, null);

        assertThat(actual, not(org.hamcrest.collection.IsEmptyCollection.empty()));
    }

    @Test
    void shouldFetchTheNextBatchOfCustomerObjects() {
        List<String> expansionFields = new ArrayList<>();
        expansionFields.add("KontaktPersoner");
        List<CustomerDTO> firstBatch = service.fetchCustomersJSON("", "Node", expansionFields, null);
        List<CustomerDTO> secondBatch = service.fetchCustomersJSON("", "Node", expansionFields, 100);

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
}
