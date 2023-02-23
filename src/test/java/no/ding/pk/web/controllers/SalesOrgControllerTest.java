package no.ding.pk.web.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.ding.pk.service.sap.SalesOrgService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@WebMvcTest(SalesOrgController.class)
public class SalesOrgControllerTest {

    @MockBean
    private SalesOrgService service;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mvc;
    
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


        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).params(params))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertThat(mvcResult.getResponse().getStatus(), is(200));
    }


}
