package no.ding.pk.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@Disabled
@Profile("itest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
public class StandardPriceControllerTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetMaterialBySalesOrgSalesOfficeAndMaterialNumber() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/standard-price/100/100/50101", String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), notNullValue());

        List<MaterialStdPriceDTO> stdPriceList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String jsonString2 = jsonObject.toString();
            MaterialStdPriceDTO stdPrice = objectMapper.readValue(jsonString2, MaterialStdPriceDTO.class);

            stdPriceList.add(stdPrice);
        }

        assertThat(stdPriceList, hasSize(greaterThan(0)));
        assertThat(stdPriceList.get(0).getMaterial(), equalTo("50101"));
    }

    @Test
    public void shouldGetAllMaterialBySalesOrgSalesOffice() throws IOException {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/standard-price/100/100", String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), notNullValue());

        List<MaterialStdPriceDTO> stdPriceList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String jsonString2 = jsonObject.toString();
            MaterialStdPriceDTO stdPrice = objectMapper.readValue(jsonString2, MaterialStdPriceDTO.class);

            stdPriceList.add(stdPrice);
        }

        assertThat(stdPriceList, hasSize(greaterThan(0)));
        assertThat(stdPriceList.get(3).getMaterialData(), notNullValue());
    }

    @Test
    public void shouldGetAllMaterialBySalesOrgSalesOfficeForGivenZone() throws IOException {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("zone", "01");
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/standard-price/100/100?zone={zone}", String.class, urlVariables);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), notNullValue());

        List<MaterialStdPriceDTO> stdPriceList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String jsonString2 = jsonObject.toString();
            MaterialStdPriceDTO stdPrice = objectMapper.readValue(jsonString2, MaterialStdPriceDTO.class);

            stdPriceList.add(stdPrice);
        }

        assertThat(stdPriceList, hasSize(greaterThan(0)));

        boolean allZonesIsSet = true;
        for(MaterialStdPriceDTO stdPriceDTO : stdPriceList) {
            if(StringUtils.isBlank(stdPriceDTO.getZone())) {
                allZonesIsSet = false;
                break;
            }
        }

        assertThat(allZonesIsSet, is(true));
    }
}
