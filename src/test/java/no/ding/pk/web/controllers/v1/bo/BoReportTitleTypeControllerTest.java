package no.ding.pk.web.controllers.v1.bo;

import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.KeyCombination;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.web.dto.v1.bo.ConditionCodeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
class BoReportTitleTypeControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BoReportConditionCodeService service;

    @BeforeEach
    public void setup() {
        ConditionCode conditionCode = ConditionCode.builder()
                .code("ZPTR")
                .build();

        KeyCombination keyCombination = KeyCombination.builder()
                .keyCombination("A615")
                .description("Salgskontor per material per sone")
                .build();

        conditionCode.addKeyCombination(keyCombination);

        service.save(conditionCode);
    }

    @Test
    public void shouldListAllTitleTypes() {
        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/bo-report/title-type/list", ConditionCodeDTO[].class);

        assertThat(responseEntity.getBody(), notNullValue());

        ConditionCodeDTO actual = responseEntity.getBody()[0];
        assertThat(actual.getKeyCombinations(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldReturnEmptyListIfTitleTypeIsNotFound() {
        Map<String, String> params = Map.of("type", "ZR05");
        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/bo-report/title-type/list?type={type}", ConditionCodeDTO[].class,params);

        assertThat(responseEntity.getBody(), notNullValue());

        ConditionCodeDTO[] actual = responseEntity.getBody();
        assertThat(actual, arrayWithSize(0));
    }

    @Test
    public void shouldReturnListIfTitleTypeIsFound() {
        Map<String, String> params = Map.of("type", "ZPTR");
        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/bo-report/title-type/list?type={type}", ConditionCodeDTO[].class,params);

        assertThat(responseEntity.getBody(), notNullValue());

        ConditionCodeDTO[] actual = responseEntity.getBody();
        assertThat(actual, arrayWithSize(1));
    }
}