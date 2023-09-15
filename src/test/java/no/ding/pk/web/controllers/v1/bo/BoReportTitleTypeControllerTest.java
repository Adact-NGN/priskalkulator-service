package no.ding.pk.web.controllers.v1.bo;

import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.web.dto.v1.bo.ConditionCodeDTO;
import no.ding.pk.web.dto.v1.bo.KeyCombinationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource("/h2-db.properties")
//@Sql(value = {
//        "/conditional_code_key_combination_scripts/drop_schemas.sql",
//        "/conditional_code_key_combination_scripts/create_condition_code.sql",
//        "/conditional_code_key_combination_scripts/create_key_combination.sql"
//}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {
//        "/conditional_code_key_combination_scripts/insert_condition_code_with_key_combination.sql"
//}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@WebMvcTest(controllers = BoReportConditionCodeController.class)
class BoReportTitleTypeControllerTest {

//    @Autowired
    private MockMvc mockMvc;

//    @LocalServerPort
//    private int serverPort;

//    @Autowired
//    private TestRestTemplate restTemplate;

    @MockBean
    private BoReportConditionCodeService service;

    @MockBean
    private PriceOfferService priceOfferService;

    @MockBean
    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void shouldListAllConditionCodes() throws Exception {

        mockMvc.perform(get("/list")).andDo(print()).andExpect(status().isOk());

//        assertThat(responseEntity.getBody(), notNullValue());
//
//        List<ConditionCodeDTO> actual = List.of(responseEntity.getBody());
//        assertThat(actual, hasSize(greaterThan(0)));
//
//        ConditionCodeDTO firstActual = actual.get(0);
//        assertThat(firstActual.getCode(), notNullValue());
//        assertThat(firstActual.getKeyCombinations(), hasSize(greaterThan(0)));
    }

//    @Test
//    public void shouldReturnEmptyListIfConditionCodeIsNotFound() {
//        Map<String, String> params = Map.of("code", "ZR0X");
//        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity(baseUrl + "/list?code={code}", ConditionCodeDTO[].class,params);
//
//        assertThat(responseEntity.getBody(), notNullValue());
//
//        ConditionCodeDTO[] actual = responseEntity.getBody();
//        assertThat(actual, arrayWithSize(0));
//    }
//
//    @Test
//    public void shouldReturnListIfConditionCodeIsFound() {
//        Map<String, String> params = Map.of("code", "ZPTR");
//        ResponseEntity<ConditionCodeDTO[]> responseEntity = restTemplate.getForEntity(baseUrl + "/list?code={code}", ConditionCodeDTO[].class,params);
//
//        assertThat(responseEntity.getBody(), notNullValue());
//
//        ConditionCodeDTO[] actual = responseEntity.getBody();
//        assertThat(actual, arrayWithSize(1));
//    }
//
//    @Test
//    public void shouldReturnAllKeyCombinationsWhenCodeIsNotGiven() {
//        ResponseEntity<KeyCombinationDTO[]> responseEntity  = restTemplate.getForEntity(baseUrl + "/list/key-combination", KeyCombinationDTO[].class);
//
//        assertThat(responseEntity.getStatusCode(), org.hamcrest.Matchers.equalTo(HttpStatus.OK));
//    }
}