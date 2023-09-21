package no.ding.pk.web.controllers;

import no.ding.pk.config.ObjectMapperConfig;
import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled("ObjectMapper is null")
@Import({SecurityTestConfig.class, ModelMapperV2Config.class, ObjectMapperConfig.class})
@WebMvcTest(SalesOrgController.class)
public class SalesOrgControllerTest {

//    @Autowired
//    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void shouldFindSalesOrgByPostalCode() throws Exception {
        String uri = "/api/salesorg";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("salesOrg", "3933");
        params.add("salesOffice", "3933");
        params.add("postalCode", "3933");
        params.add("salesZone", "3933");
        params.add("city", "3933");
        params.add("skiptokens", "0");

        mockMvc.perform(get(uri).params(params))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

    @Test
    public void shouldFindSalesOrgByPostalCodeWhitSkipTokens() throws Exception {
        String uri = "/api/salesorg";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("salesOrg", "3933");
        params.add("salesOffice", "3933");
        params.add("postalCode", "3933");
        params.add("salesZone", "3933");
        params.add("city", "3933");
        params.add("skiptokens", "1");

        mockMvc.perform(get(uri).params(params))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }
}
