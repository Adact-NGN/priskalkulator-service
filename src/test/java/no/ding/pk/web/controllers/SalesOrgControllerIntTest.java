package no.ding.pk.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.web.dto.sap.SalesOrgDTO;
import no.ding.pk.web.dto.web.client.ZoneDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Disabled
@Profile("itest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
public class SalesOrgControllerIntTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetSalesOfficeWhenSearchingWithPostalCodeForNotodden() {
        String url = "http://localhost:{serverPort}/api/v1/salesorg";
        String formattedUrl = url +
                "?" +
                "salesOrg={salesOrg}" + "&" +
                "salesOffice={salesOffice}" + "&" +
                "postalCode={postalCode}" + "&" +
                "salesZone={salesZone}" + "&" +
                "city={city}";

        String postalCode = "3677";
        Map<String, String> params = Map
                .of("serverPort", String.valueOf(serverPort),
                        "salesOrg", postalCode,
                        "salesOffice", postalCode,
                        "postalCode", postalCode,
                        "salesZone", postalCode,
                        "city", postalCode);

        ResponseEntity<SalesOrgDTO[]> result = this.restTemplate.getForEntity(formattedUrl, SalesOrgDTO[].class, params);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody(), arrayWithSize(greaterThan(0)));
        SalesOrgDTO actual = result.getBody()[0];
        assertThat(actual.getPostalCode(), is(postalCode));
    }

    @Test
    public void shouldGetSalesOfficeWhenSearchingWithPostalCodeForMandal() {
        String url = "http://localhost:{serverPort}/api/v1/salesorg";
        String formattedUrl = url +
                "?" +
                "salesOrg={salesOrg}" + "&" +
                "salesOffice={salesOffice}" + "&" +
                "postalCode={postalCode}" + "&" +
                "salesZone={salesZone}" + "&" +
                "city={city}";

        String postalCode = "4514";
        Map<String, String> params = Map
                .of("serverPort", String.valueOf(serverPort),
                        "salesOrg", postalCode,
                        "salesOffice", postalCode,
                        "postalCode", postalCode,
                        "salesZone", postalCode,
                        "city", postalCode);

        ResponseEntity<SalesOrgDTO[]> result = this.restTemplate.getForEntity(formattedUrl, SalesOrgDTO[].class, params);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody(), arrayWithSize(greaterThan(0)));
        SalesOrgDTO actual = result.getBody()[0];
        assertThat(actual.getPostalCode(), is(postalCode));
    }

    @Test
    public void shouldGetAllZonesForSalesOffice() throws Exception {

        String url = "http://localhost:" + serverPort + "/api/v1/salesorg/100/104/zones";

        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(url, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), notNullValue());

        List<ZoneDTO> salesOrgDTOList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            ZoneDTO salesOrgDTO = objectMapper.readValue(jsonObject.toString(), ZoneDTO.class);

            salesOrgDTOList.add(salesOrgDTO);
        }

        assertThat(salesOrgDTOList, hasSize(greaterThan(0)));

        List<ZoneDTO> defaultZones = salesOrgDTOList.stream().filter(zoneDto -> zoneDto.getIsStandardZone() != null && zoneDto.getIsStandardZone()).collect(Collectors.toList());

        assertThat(defaultZones, hasSize(0));
    }

    @Test
    public void shouldGetAllZonesForSalesOfficeAndGetWhichZoneIsTheDefaultZone() throws JsonProcessingException {
        String url = "http://localhost:" + serverPort + "/api/v1/salesorg/100/104/zones?postalCode=3933";

        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(url, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), notNullValue());

        List<ZoneDTO> salesOrgDTOList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            ZoneDTO salesOrgDTO = objectMapper.readValue(jsonObject.toString(), ZoneDTO.class);

            salesOrgDTOList.add(salesOrgDTO);
        }

        assertThat(salesOrgDTOList, hasSize(greaterThan(0)));

        List<ZoneDTO> defaultZones = salesOrgDTOList.stream().filter(zoneDto -> zoneDto.getIsStandardZone() != null && zoneDto.getIsStandardZone()).collect(Collectors.toList());

        assertThat(defaultZones, hasSize(greaterThan(0)));

    }

    @Test
    public void shouldGetAllZonesForSalesOfficeAndSalesZoneIsEmpty() throws JsonProcessingException {
        String url = "http://localhost:" + serverPort + "/api/v1/salesorg/100/104/zones?postalCode=3671";

        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(url, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), notNullValue());

        List<ZoneDTO> salesOrgDTOList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseEntity.getBody());

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            ZoneDTO salesOrgDTO = objectMapper.readValue(jsonObject.toString(), ZoneDTO.class);

            salesOrgDTOList.add(salesOrgDTO);
        }

        assertThat(salesOrgDTOList, hasSize(greaterThan(0)));

        List<ZoneDTO> defaultZones = salesOrgDTOList.stream().filter(zoneDto -> zoneDto.getIsStandardZone() != null && zoneDto.getIsStandardZone()).collect(Collectors.toList());

        assertThat(defaultZones, hasSize(0));
    }
}
