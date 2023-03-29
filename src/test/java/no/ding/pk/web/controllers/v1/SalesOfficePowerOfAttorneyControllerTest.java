package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.User;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.web.dto.web.client.SalesOfficePowerOfAttorneyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
@Sql(value = { "/power_of_attorney/drop_schema.sql", "/power_of_attorney/create_schema.sql"})
@Sql(value = { "/power_of_attorney/power_of_attorney.sql"})
class SalesOfficePowerOfAttorneyControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldGetAllPowerOfAttorney() {
        ResponseEntity<SalesOfficePowerOfAttorneyDTO[]> result = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/list", SalesOfficePowerOfAttorneyDTO[].class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        assertThat(result.getBody(), arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldCreateNewPowerOfAttorney() {
        SalesOfficePowerOfAttorneyDTO sopoa = SalesOfficePowerOfAttorneyDTO.builder()
                .salesOffice(900)
                .salesOfficeName("MegaOslo")
                .region("Verden")
                .build();

        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        assertThat(result.getBody(), notNullValue());

        SalesOfficePowerOfAttorneyDTO resultSopoa = result.getBody();

        assertThat(resultSopoa.getId(), notNullValue());
    }

    @Test
    public void shouldDeletePowerOfAttorney() {
        SalesOfficePowerOfAttorneyDTO sopoa = SalesOfficePowerOfAttorneyDTO.builder()
                .salesOffice(900)
                .salesOfficeName("MegaOslo")
                .region("Verden")
                .build();

        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", String.valueOf(result.getBody().getId()));
        restTemplate.delete("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/delete/{id}", urlVariables);

        result = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/id/{id}", SalesOfficePowerOfAttorneyDTO.class, urlVariables);

        assertThat(result.getBody(), is(nullValue()));
    }

    @Test
    public void shouldCreatePowerOfAttorneyWithUsers() {
        User user = User.builder()
                .adId("dc804853-6a82-4022-8eb5-244fff724af2")
                .associatedPlace("Larvik")
                .email("kjetil.torvund.minde@ngn.no")
                .fullName("Kjetil Torvund Minde")
                .name("Kjetil")
                .powerOfAttorneyOA(5)
                .powerOfAttorneyFA(5)
                .build();

        user = userRepository.save(user);

        SalesOfficePowerOfAttorneyDTO sopoa = SalesOfficePowerOfAttorneyDTO.builder()
                .salesOffice(900)
                .salesOfficeName("MegaOslo")
                .region("Verden")
                .mailOrdinaryWasteLvlOne(user.getEmail())
                .mailOrdinaryWasteLvlTwo(user.getEmail())
                .dangerousWaste(user.getEmail())
                .build();

        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        assertThat(result.getBody(), notNullValue());

        SalesOfficePowerOfAttorneyDTO resultSopoa = result.getBody();

        assertThat(resultSopoa.getId(), notNullValue());

        assertThat(resultSopoa.getMailOrdinaryWasteLvlOne(), equalTo(user.getEmail()));
        assertThat(resultSopoa.getMailOrdinaryWasteLvlTwo(), equalTo(user.getEmail()));
        assertThat(resultSopoa.getDangerousWaste(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldUpdatePowerOfAttorneyWithUsers() {
        User user = User.builder()
                .adId("dc804853-6a82-4022-8eb5-244fff724af2")
                .associatedPlace("Larvik")
                .email("kjetil.torvund.minde@ngn.no")
                .phoneNumber("+4790135757")
                .fullName("Kjetil Torvund Minde")
                .name("Kjetil")
                .powerOfAttorneyOA(5)
                .powerOfAttorneyFA(5)
                .build();

        user = userRepository.save(user);

        User otherUser = User.builder()
                .adId("7c6c0b5c-53de-4224-ac98-aa92c1aaa2ef")
                .associatedPlace("Larvik")
                .email("kristin.nerum@ngn.no")
                .phoneNumber("+4798232574")
                .fullName("Kristin Jørgensen Nærum")
                .name("Kristin")
                .powerOfAttorneyOA(2)
                .powerOfAttorneyFA(3)
                .build();

        otherUser = userRepository.save(otherUser);

        SalesOfficePowerOfAttorneyDTO sopoa = SalesOfficePowerOfAttorneyDTO.builder()
                .salesOffice(900)
                .salesOfficeName("MegaOslo")
                .region("Verden")
                .mailOrdinaryWasteLvlOne(user.getEmail())
                .mailOrdinaryWasteLvlTwo(user.getEmail())
                .dangerousWaste(user.getEmail())
                .build();


        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        assertThat(result.getBody(), notNullValue());

        sopoa = result.getBody();
        sopoa.setMailOrdinaryWasteLvlOne(otherUser.getEmail());
        sopoa.setMailOrdinaryWasteLvlTwo(otherUser.getEmail());
        sopoa.setDangerousWaste(otherUser.getEmail());

        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("id", String.valueOf(sopoa.getId()));
        restTemplate.put("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/save/{id}", sopoa, urlVariables);

        result = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/id/{id}", SalesOfficePowerOfAttorneyDTO.class, urlVariables);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        SalesOfficePowerOfAttorneyDTO resultSopoa = result.getBody();

        assertThat(resultSopoa.getId(), notNullValue());

        assertThat(resultSopoa.getMailOrdinaryWasteLvlOne(), equalTo(otherUser.getEmail()));
        assertThat(resultSopoa.getMailOrdinaryWasteLvlTwo(), equalTo(otherUser.getEmail()));
        assertThat(resultSopoa.getDangerousWaste(), equalTo(otherUser.getEmail()));
    }
}