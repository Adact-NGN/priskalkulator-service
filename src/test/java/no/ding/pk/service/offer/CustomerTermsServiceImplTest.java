package no.ding.pk.service.offer;

import java.util.Date;

import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.repository.offer.CustomerTermsRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
public class CustomerTermsServiceImplTest {

    @Autowired
    private CustomerTermsRepository repository;

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
    void testFindActiveTermsForCustomerForSalesOfficeAndSalesOrgWhereEndDateIsInTheFuture() {

        String customerNumber = "295843";
        String salesOffice = "100";
        String salesOrg = "100";

        LocalDateTime currentDateAndTime = LocalDateTime.now();

        CustomerTerms customerTerms = CustomerTerms.builder()
        .customerNumber(customerNumber)
        .salesOffice(salesOffice)
        .salesOrg(salesOrg)
        .agreementStartDate(new Date())
        .agreementEndDate(currentDateAndTime.plusYears(1).toDate())
        .build();

        service.save(salesOffice, customerNumber, customerTerms);

        CustomerTerms actual = service.findActiveTermsForCustomerForSalesOfficeAndSalesOrg(customerNumber, salesOffice, salesOrg);

        assertThat(actual, notNullValue());
    }
}
