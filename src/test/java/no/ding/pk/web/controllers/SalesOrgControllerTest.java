package no.ding.pk.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SalesOrgControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
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


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).params(params))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertThat(mvcResult.getResponse().getStatus(), is(200));
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


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).params(params))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertThat(mvcResult.getResponse().getStatus(), is(200));
    }
}
