package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.CustomerTerms;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
@SqlConfig(commentPrefix = "#")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(value = {"/customerTerms_db_scripts/drop_schema.sql", "/customerTerms_db_scripts/create_schema.sql"})
class CustomerTermsRepositoryTest {

    @Autowired
    private CustomerTermsRepository repository;

    @Test
    public void shouldPersistCustomerTerms() {
        CustomerTerms customerTerms = CustomerTerms.builder()
                .salesOrg("100")
                .customerNumber("12345678")
                .customerName("Test Customer")
                .salesOffice("104")
                .agreementStartDate(new Date())
                .build();

        CustomerTerms actual = repository.save(customerTerms);

        assertThat(actual, notNullValue());
    }

    @Test
    public void shouldFindAllActiveCustomerTerms() {
        LocalDateTime localDateTime = new LocalDateTime();
        String salesOffice = "104";
        String customerNumber = "12345678";
        String salesOrg = "100";
        List<CustomerTerms> customerTerms = List.of(CustomerTerms.builder()
                        .salesOrg(salesOrg)
                        .customerNumber(customerNumber)
                        .customerName("Test Customer")
                        .salesOffice(salesOffice)
                        .agreementStartDate(localDateTime.minusYears(4).toDate())
                        .agreementEndDate(localDateTime.minusYears(3).toDate())
                        .build(),
                CustomerTerms.builder()
                        .salesOrg(salesOrg)
                        .customerNumber(customerNumber)
                        .customerName("Test Customer")
                        .salesOffice(salesOffice)
                        .agreementStartDate(localDateTime.minusYears(3).toDate())
                        .agreementEndDate(localDateTime.minusYears(2).toDate())
                        .build(),
                CustomerTerms.builder()
                        .salesOrg(salesOrg)
                        .customerNumber(customerNumber)
                        .customerName("Test Customer")
                        .salesOffice(salesOffice)
                        .agreementStartDate(localDateTime.minusYears(2).toDate())
//                        .agreementEndDate(localDateTime.minusYears(1).toDate())
                        .build());

        repository.saveAll(customerTerms);

        List<CustomerTerms> acutal = repository.findBySalesOrgAndSalesOfficeAndCustomerNumberAndAgreementEndDateGreaterThanOrAgreementEndDateIsNullOrderByCreatedDateDesc(salesOrg, salesOffice, customerNumber, null);

        assertThat(acutal, hasSize(1));
    }

}