package no.ding.pk.web.controllers.v1.bo;

import no.ding.pk.repository.bo.ConditionCodeRepository;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.web.dto.v1.bo.ConditionCodeDTO;
import no.ding.pk.web.dto.v1.bo.KeyCombinationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
@Sql(value = {
        "/conditional_code_key_combination_scripts/drop_schemas.sql",
        "/conditional_code_key_combination_scripts/create_condition_code.sql",
        "/conditional_code_key_combination_scripts/create_key_combination.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {
        "/conditional_code_key_combination_scripts/insert_condition_code_with_key_combination.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BoReportTitleTypeControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BoReportConditionCodeService service;

    @Autowired
    private ConditionCodeRepository conditionCodeRepository;
    private String baseUrl;



    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + serverPort + "/api/v1/bo-report/condition-code";
//        ConditionCode conditionCode = ConditionCode.builder()
//                .code("ZPTR")
//                .build();
//
//        KeyCombination keyCombination = KeyCombination.builder()
//                .keyCombination("A615")
//                .description("Salgskontor per material per sone")
//                .build();
//
//        conditionCode.addKeyCombination(keyCombination);
//
//        service.save(conditionCode);
    }

    @Test
    public void shouldListAllConditionCodes() {

        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity(baseUrl + "/list", ConditionCodeDTO[].class);

        assertThat(responseEntity.getBody(), notNullValue());

        List<ConditionCodeDTO> actual = List.of(responseEntity.getBody());
        assertThat(actual, hasSize(greaterThan(0)));

        ConditionCodeDTO firstActual = actual.get(0);
        assertThat(firstActual.getCode(), notNullValue());
        assertThat(firstActual.getKeyCombinations(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldReturnEmptyListIfConditionCodeIsNotFound() {
        Map<String, String> params = Map.of("code", "ZR05");
        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity(baseUrl + "/list?type={code}", ConditionCodeDTO[].class,params);

        assertThat(responseEntity.getBody(), notNullValue());

        ConditionCodeDTO[] actual = responseEntity.getBody();
        assertThat(actual, arrayWithSize(0));
    }

    @Test
    public void shouldReturnListIfConditionCodeIsFound() {
        Map<String, String> params = Map.of("code", "ZPTR");
        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity(baseUrl + "/list?type={code}", ConditionCodeDTO[].class,params);

        assertThat(responseEntity.getBody(), notNullValue());

        ConditionCodeDTO[] actual = responseEntity.getBody();
        assertThat(actual, arrayWithSize(1));
    }

    @Test
    public void shouldReturnAllKeyCombinationsWhenCodeIsNotGiven() {
        ResponseEntity<KeyCombinationDTO[]> responseEntity  = restTemplate.getForEntity(baseUrl + "/list/key-combination", KeyCombinationDTO[].class);

        assertThat(responseEntity.getStatusCode(), org.hamcrest.Matchers.equalTo(HttpStatus.OK));
    }
}