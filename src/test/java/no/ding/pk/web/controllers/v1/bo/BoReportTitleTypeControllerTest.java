package no.ding.pk.web.controllers.v1.bo;

import com.google.gson.Gson;
import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.KeyCombination;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.service.offer.PriceOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityTestConfig.class, ModelMapperV2Config.class})
@WebMvcTest(controllers = BoReportConditionCodeController.class)
class BoReportTitleTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoReportConditionCodeService service;

    @MockBean
    private PriceOfferService priceOfferService;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;

    @MockBean
    private JwtDecoder jwtDecoder;
    private final String baseUrl = "/api/v1/bo-report/condition-code";

    @MockBean
    private MaterialService materialService;

    @MockBean
    private SalesRoleRepository salesRoleRepository;

    @BeforeEach
    public void setup() {

    }

    @Test
    @WithMockUser("alex")
    public void shouldListAllConditionCodes() throws Exception {

        List<KeyCombination> keyCombinations = List.of(KeyCombination
                .builder()
                        .keyCombination("A615")
                        .description("Salgskontor per material per sone")
                .build());
        List<ConditionCode> conditionCodes = List.of(ConditionCode.builder()
                        .code("ZPTR")
                        .keyCombinations(keyCombinations)
                .build());
        when(service.getAllConditionCodes(anyString())).thenReturn(conditionCodes);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "ZPTR");
        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/list").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();

        Gson gson = new Gson();
        ConditionCode[] actual = gson.fromJson(jsonString, ConditionCode[].class);

        assertThat(actual, arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldReturnEmptyListIfConditionCodeIsNotFound() throws Exception {
       MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "ZR0X");
        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/list").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();

        Gson gson = new Gson();
        ConditionCode[] actual = gson.fromJson(jsonString, ConditionCode[].class);

        assertThat(actual, arrayWithSize(0));
    }

    @Test
    public void shouldReturnAllKeyCombinationsWhenCodeIsNotGiven() throws Exception {
        List<KeyCombination> keyCombinations = List.of(
                KeyCombination
                        .builder()
                        .keyCombination("A615")
                        .description("Salgskontor per material per sone")
                        .build(),
                KeyCombination
                        .builder()
                        .keyCombination("A783")
                        .description("Salgskontor per material per avfall per sone")
                        .build()
        );

        when(service.getKeyCombinationList()).thenReturn(keyCombinations);
        MvcResult mvcResult = mockMvc.perform(get(baseUrl + "/list/key-combination"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();

        Gson gson = new Gson();
        ConditionCode[] actual = gson.fromJson(jsonString, ConditionCode[].class);

        assertThat(actual, arrayWithSize(2));
    }
}