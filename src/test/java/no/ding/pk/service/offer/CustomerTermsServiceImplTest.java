package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.CustomerTerms;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
public class CustomerTermsServiceImplTest {

    @Autowired
    private CustomerTermsService service;

    @Test
    void testFindActiveTermsForCustomerForSalesOfficeAndSalesOrgWhereEndDateIsNull() {

        String customerNumber = "295843";
        String salesOffice = "100";
        String salesOrg = "100";

        CustomerTerms customerTerms = CustomerTerms.builder()
        .customerNumber(customerNumber)
        .salesOffice(salesOffice)
        .salesOrg(salesOrg)
        .agreementStartDate(new Date())
        .agreementEndDate(null)
        .build();

        service.save(salesOffice, customerNumber, customerTerms);

        CustomerTerms actual = service.findActiveTermsForCustomerForSalesOfficeAndSalesOrg(customerNumber, salesOffice, salesOrg);

        assertThat(actual, notNullValue());
    }

    @Test
    void testFindActiveTermsForCustomerForSalesOfficeAndSalesOrgWhereEndDateIsInTheFuture() throws InterruptedException {

        String customerNumber = "295843";
        String salesOffice = "100";
        String salesOrg = "100";

        LocalDateTime currentDateAndTime = LocalDateTime.now();

        CustomerTerms customerTerms = CustomerTerms.builder()
        .customerNumber(customerNumber)
        .salesOffice(salesOffice)
        .salesOrg(salesOrg)
        .agreementStartDate(currentDateAndTime.toDate())
        .agreementEndDate(currentDateAndTime.plusYears(1).toDate())
        .build();

        service.save(salesOffice, customerNumber, customerTerms);

        Thread.sleep(2000L);

        customerTerms = CustomerTerms.builder()
                .customerNumber(customerNumber)
                .salesOffice(salesOffice)
                .salesOrg(salesOrg)
                .agreementStartDate(currentDateAndTime.minusYears(1).toDate())
                .agreementEndDate(null)
                .build();

        service.save(salesOffice, customerNumber, customerTerms);

        CustomerTerms actual = service.findActiveTermsForCustomerForSalesOfficeAndSalesOrg(customerNumber, salesOffice, salesOrg);

        assertThat(actual, notNullValue());
    }

    @Test
    public void shouldOnlyGetLastActiveCustomerTermsForCustomer() {
        String customerNumber = "295843";
        String salesOffice = "100";
        String salesOrg = "100";

        LocalDateTime localDateTime = new LocalDateTime();

        List<CustomerTerms> customerTerms = List.of(
                CustomerTerms.builder()
                        .customerNumber(customerNumber)
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .agreementStartDate(localDateTime.minusYears(2).toDate())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(customerNumber)
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .agreementStartDate(localDateTime.minusYears(1).toDate())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(customerNumber)
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .comment("This one should be returned")
                        .agreementStartDate(localDateTime.toDate())
                        .build()
        );

        for(CustomerTerms customerTerm : customerTerms) {
            service.save(salesOffice, customerNumber, customerTerm);
        }

        CustomerTerms actual = service.findActiveTermsForCustomerForSalesOfficeAndSalesOrg(customerNumber, salesOffice, salesOrg);

        assertThat(actual.getComment(), notNullValue());
    }
}
