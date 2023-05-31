package no.ding.pk.web.controllers.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.web.dto.v1.web.client.offer.CustomerTermsDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
class CustomerTermsControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerTermsService service;

    @Test
    public void shouldGetAllCustomerTerms() {
        String salesOffice = "100";
        String customerNumber = "326380";
        CustomerTerms customerTerms = CustomerTerms.builder()
                .number(2)
                .level("Kundenivå")
                .source("PK")
                .salesOffice(salesOffice)
                .customerName("Veidekke Ulven B4 PN 36547")
                .customerNumber(customerNumber)
                .specialConditionAction("Fastpris")
                .contractTerm("NG prisvilkår")
                .quarterlyAdjustment("Q1")
                .metalPricing("Indeks")
                .salesEmployee("Charlotte Luisa")
                .comment("PS 177149. LÅSTE PRISER UT PROSJEKTET PS3 355590.")
                .region("Øst")
                .year(2022)
                .build();

        service.save(salesOffice, customerNumber, customerTerms);

        ResponseEntity<CustomerTermsDTO[]> responseEntity = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/terms/customer/list", CustomerTermsDTO[].class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

        List<CustomerTermsDTO> list = List.of(Objects.requireNonNull(responseEntity.getBody()));
        assertThat(list, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldPersistNewCustomerTerms() {
        CustomerTermsDTO customerTermsDTO = CustomerTermsDTO.builder()
                .number(504)
                .level("Kundenivå")
                .salesOffice("112")
                .customerName("Søre Sunnmøre Gjenvinning AS")
                .customerNumber("295843")
                .contractTerm("NG prisvilkår")
                .quarterlyAdjustment("Q1")
                .region("Nord-Vest")
                .year(2020)
                .priceAdjustmentDate(new Date())
                .build();

        ResponseEntity<CustomerTermsDTO> responseEntity = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/terms/customer/create", customerTermsDTO, CustomerTermsDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

        CustomerTermsDTO actual = responseEntity.getBody();

        assertThat(actual.getPriceAdjustmentDate(), notNullValue());
    }

    @Test
    public void shouldPersistNewCustomerTermsWhenGivenJsonString() {
        String json = readJsonFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        String responseEntity = restTemplate.postForObject("http://localhost:" + serverPort + "/api/v1/terms/customer/create", request, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root;
        try {
            root = objectMapper.readTree(responseEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertThat(root, notNullValue());
    }

    private String readJsonFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(Objects.requireNonNull(classLoader.getResource("customerTerms.json")).getFile());

        assertThat(file.exists(), is(true));

        String json = getStringFromFile(file);

        assertThat("JSON is empty", json, not(emptyOrNullString()));

        return json;
    }

    private static String getStringFromFile(File file) {
        try {
            return IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}