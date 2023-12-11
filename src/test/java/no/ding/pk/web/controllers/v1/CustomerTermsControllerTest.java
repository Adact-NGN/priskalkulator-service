package no.ding.pk.web.controllers.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.web.dto.v1.web.client.offer.CustomerTermsDTO;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("ObjectMapper is null")
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityTestConfig.class, ModelMapperV2Config.class})
@WebMvcTest(controllers = CustomerTermsController.class)
class CustomerTermsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerTermsService service;

    @MockBean
    private MaterialService materialService;

    @MockBean
    private SalesRoleRepository salesRoleRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;
    private String baseUrl = "/api/v1/terms/customer";

    @BeforeEach
    public void setup() {
//        service = mock(CustomerTermsService.class);
//        materialService = mock(MaterialService.class);
//        salesRoleRepository = mock(SalesRoleRepository.class);
//        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerTermsController(service, modelMapper)).build();
    }

    @Test
    @WithMockUser("alex")
    public void shouldGetAllCustomerTerms() throws Exception {
        String salesOffice = "100";
        String customerNumber = "326380";
        String customerName = "Veidekke Ulven B4 PN 36547";
        CustomerTerms customerTerms = CustomerTerms.builder()
                .number(2)
                .level("Kundenivå")
                .source("PK")
                .salesOffice(salesOffice)
                .customerName(customerName)
                .customerNumber(customerNumber)
                .specialConditionAction("Fastpris")
                .contractTerm("NG prisvilkår")
                .quarterlyAdjustment("Q1")
                .metalPricing("Indeks")
                .salesEmployee("Charlotte Luisa")
                .comment("PS 177149. LÅSTE PRISER UT PROSJEKTET PS3 355590.")
                .region("Øst")
//                .agreementStartDate(new Date())
                .year(2022)
                .build();

        List<CustomerTerms> customerTermsList = new ArrayList<>();
        customerTermsList.add(customerTerms);
        when(service.findAll(anyString(), anyString(), null)).thenReturn(customerTermsList);



        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("salesOffice", salesOffice);
        params.add("customerNumber", customerNumber);
        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/list").params(params))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        CustomerTermsDTO[] actual = getResponseBody(mvcResult, CustomerTermsDTO[].class);

        assertThat(actual, arrayWithSize(1));
    }

    private static <T> T getResponseBody(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        String jsonString = mvcResult.getResponse().getContentAsString();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
        return gson.fromJson(jsonString, clazz);
    }

    @Disabled("Move to service test")
    @Test
    public void shouldListAllActiveCustomerTerms() throws Exception {
        String firstCustomerNumber = "12345678";
        String firstCustomerName = "C1";
        String secondCustomerNumber = "87654321";
        String secondCustomerName = "C2";

        LocalDateTime localDateTime = new LocalDateTime();

        List<CustomerTerms> customerTerms = List.of(
                CustomerTerms.builder()
                        .customerNumber(firstCustomerNumber)
                        .customerName(firstCustomerName)
                        .salesOffice("100")
                        .salesOrg("100")
//                        .agreementStartDate(localDateTime.minusYears(2).toDate())
//                        .agreementEndDate(localDateTime.minusYears(1).toDate())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(firstCustomerNumber)
                        .customerName(firstCustomerName)
                        .salesOffice("100")
                        .salesOrg("100")
//                        .agreementStartDate(new Date())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(secondCustomerNumber)
                        .customerName(secondCustomerName)
                        .salesOffice("104")
                        .salesOrg("100")
//                        .agreementStartDate(localDateTime.minusYears(2).toDate())
//                        .agreementEndDate(localDateTime.minusYears(1).toDate())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(secondCustomerNumber)
                        .customerName(secondCustomerName)
                        .salesOffice("104")
                        .salesOrg("100")
//                        .agreementStartDate(new Date())
                        .build());

        when(service.findAllActive(anyString(), anyString())).thenReturn(customerTerms);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("salesOffice", "100");
        params.add("customerNumber", "100");
        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/list/active").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        CustomerTermsDTO[] actual = getResponseBody(mvcResult, CustomerTermsDTO[].class);

        assertThat(actual, arrayWithSize(2));

//        ResponseEntity<CustomerTermsDTO[]> responseEntity = restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/terms/customer/list/active",
//                CustomerTermsDTO[].class);
//
//        assertThat(responseEntity.getBody(), arrayWithSize(2));
    }

    @Disabled("Move to Service test")
    @Test
    public void shouldListAllActiveCustomerTermsFilteredByCustomerAndCustomerNumber() {
        String firstCustomerNumber = "12345678";
        String firstCustomerName = "C1";
        String secondCustomerNumber = "87654321";
        String secondCustomerName = "C2";

        LocalDateTime localDateTime = new LocalDateTime();

        List<CustomerTerms> customerTerms = List.of(
                CustomerTerms.builder()
                        .customerNumber(firstCustomerNumber)
                        .customerName(firstCustomerName)
                        .salesOffice("100")
                        .salesOrg("100")
                        .agreementStartDate(localDateTime.minusYears(2).toDate())
                        .agreementEndDate(localDateTime.minusYears(1).toDate())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(firstCustomerNumber)
                        .customerName(firstCustomerName)
                        .salesOffice("100")
                        .salesOrg("100")
                        .agreementStartDate(new Date())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(secondCustomerNumber)
                        .customerName(secondCustomerName)
                        .salesOffice("104")
                        .salesOrg("100")
                        .agreementStartDate(localDateTime.minusYears(2).toDate())
                        .agreementEndDate(localDateTime.minusYears(1).toDate())
                        .build(),
                CustomerTerms.builder()
                        .customerNumber(secondCustomerNumber)
                        .customerName(secondCustomerName)
                        .salesOffice("104")
                        .salesOrg("100")
                        .agreementStartDate(new Date())
                        .build());

        for(CustomerTerms ct: customerTerms) {
            service.save(ct.getSalesOffice(), ct.getCustomerNumber(), ct.getCustomerName(), ct);
        }

        Map<String, String> params = Map.of("salesOffice", "100", "customerNumber", firstCustomerNumber);
    }

    @Test
    public void shouldPersistNewCustomerTermsWhenGivenJsonString() throws Exception {
        String json = readJsonFile();

        when(service.save(ArgumentMatchers.any())).thenAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            return (CustomerTerms) arguments[0];
        });

        mockMvc.perform(post(baseUrl + "/create")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
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