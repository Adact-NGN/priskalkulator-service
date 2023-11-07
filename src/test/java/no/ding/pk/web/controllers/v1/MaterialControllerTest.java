package no.ding.pk.web.controllers.v1;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Disabled("Move test to service.")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
public class MaterialControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldGetMaterialBySalesOrgSalesOfficeAndMaterialNumber() {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/material/100/100/50101", String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }
}
