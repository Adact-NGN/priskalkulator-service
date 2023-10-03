package no.ding.pk.web.controllers.v1;

import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.utils.JsonTestUtils;
import no.ding.pk.web.dto.v2.web.client.SalesOfficePowerOfAttorneyDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("ObjectMapper is null")
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityTestConfig.class, ModelMapperV2Config.class})
@WebMvcTest(SalesOfficePowerOfAttorneyController.class)
class SalesOfficePowerOfAttorneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SalesOfficePowerOfAttorneyService service;

    @MockBean
    private MaterialService materialService;

    @MockBean
    private SalesRoleRepository salesRoleRepository;

    private static String baseUrl = "/api/v1/sales-office-power-of-attorney";

    @Test
    public void shouldGetAllPowerOfAttorney() throws Exception {
        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder().build();

        when(service.findAll()).thenReturn(List.of(powerOfAttorney));

        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/list"))
                .andExpect(status().isOk())
                .andReturn();

        SalesOfficePowerOfAttorneyDTO[] actual = JsonTestUtils.getResponseBody(mvcResult, SalesOfficePowerOfAttorneyDTO[].class);

        assertThat(actual, arrayWithSize(greaterThan(0)));
    }

    @Disabled("Move to service test")
    @Test
    public void shouldCreateNewPowerOfAttorney() {
        SalesOfficePowerOfAttorneyDTO sopoa = SalesOfficePowerOfAttorneyDTO.builder()
                .salesOffice(900)
                .salesOfficeName("MegaOslo")
                .region("Verden")
                .build();

//        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);
//
//        assertThat(result.getStatusCode(), is(HttpStatus.OK));
//
//        assertThat(result.getBody(), notNullValue());
//
//        SalesOfficePowerOfAttorneyDTO resultSopoa = result.getBody();
//
//        assertThat(resultSopoa.getId(), notNullValue());
    }

    @Disabled("Move to service test")
    @Test
    public void shouldDeletePowerOfAttorney() {
        SalesOfficePowerOfAttorneyDTO sopoa = SalesOfficePowerOfAttorneyDTO.builder()
                .salesOffice(900)
                .salesOfficeName("MegaOslo")
                .region("Verden")
                .build();

//        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);
//
//        assertThat(result.getStatusCode(), is(HttpStatus.OK));
//
//        Map<String, String> urlVariables = new HashMap<>();
//        urlVariables.put("id", String.valueOf(result.getBody().getId()));
//        restTemplate.delete("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/delete/{id}", urlVariables);
//
//        result = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/id/{id}", SalesOfficePowerOfAttorneyDTO.class, urlVariables);
//
//        assertThat(result.getBody(), is(nullValue()));
    }

    @Disabled("Move to service test")
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

//        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);
//
//        assertThat(result.getStatusCode(), is(HttpStatus.OK));
//
//        assertThat(result.getBody(), notNullValue());
//
//        SalesOfficePowerOfAttorneyDTO resultSopoa = result.getBody();
//
//        assertThat(resultSopoa.getId(), notNullValue());
//
//        assertThat(resultSopoa.getMailOrdinaryWasteLvlOne(), equalTo(user.getEmail()));
//        assertThat(resultSopoa.getMailOrdinaryWasteLvlTwo(), equalTo(user.getEmail()));
//        assertThat(resultSopoa.getDangerousWaste(), equalTo(user.getEmail()));
    }

    @Disabled("Move to service test")
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


//        ResponseEntity<SalesOfficePowerOfAttorneyDTO> result = restTemplate.postForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/create", sopoa, SalesOfficePowerOfAttorneyDTO.class);
//
//        assertThat(result.getStatusCode(), is(HttpStatus.OK));
//
//        assertThat(result.getBody(), notNullValue());
//
//        sopoa = result.getBody();
//        sopoa.setMailOrdinaryWasteLvlOne(otherUser.getEmail());
//        sopoa.setMailOrdinaryWasteLvlTwo(otherUser.getEmail());
//        sopoa.setDangerousWaste(otherUser.getEmail());
//
//        Map<String, String> urlVariables = new HashMap<>();
//        urlVariables.put("id", String.valueOf(sopoa.getId()));
//        restTemplate.put("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/save/{id}", sopoa, urlVariables);
//
//        result = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/sales-office-power-of-attorney/id/{id}", SalesOfficePowerOfAttorneyDTO.class, urlVariables);
//
//        assertThat(result.getStatusCode(), is(HttpStatus.OK));
//
//        SalesOfficePowerOfAttorneyDTO resultSopoa = result.getBody();
//
//        assertThat(resultSopoa.getId(), notNullValue());
//
//        assertThat(resultSopoa.getMailOrdinaryWasteLvlOne(), equalTo(otherUser.getEmail()));
//        assertThat(resultSopoa.getMailOrdinaryWasteLvlTwo(), equalTo(otherUser.getEmail()));
//        assertThat(resultSopoa.getDangerousWaste(), equalTo(otherUser.getEmail()));
    }
}