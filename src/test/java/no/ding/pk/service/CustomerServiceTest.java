package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import no.ding.pk.repository.CustomerRepository;

public class CustomerServiceTest {

    private CustomerRepository customerRepository = mock(CustomerRepository.class);
    private CustomerService service = new CustomerService();

    @Test
    void testFetchCustomers() throws IOException, URISyntaxException, InterruptedException {
        String actual = service.fetchCustomersJSON();

        assertThat(actual, not(emptyOrNullString()));
    }
}
