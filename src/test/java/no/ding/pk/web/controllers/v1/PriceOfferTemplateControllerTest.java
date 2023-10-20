package no.ding.pk.web.controllers.v1;

import com.azure.spring.cloud.autoconfigure.aad.properties.AadResourceServerProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import no.ding.pk.config.ObjectMapperTestConfig;
import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v1.ModelMapperConfig;
import no.ding.pk.web.dto.web.client.offer.template.PriceOfferTemplateDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Import({SecurityTestConfig.class, JacksonAutoConfiguration.class})
@WebMvcTest(PriceOfferTemplateController.class)
public class PriceOfferTemplateControllerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private AadResourceServerProperties aadResourceServerProperties;

    @Test
    public void shouldPersistNewPriceOfferTemplate() throws Exception {
        PriceOfferTemplateDTO templateDTO = PriceOfferTemplateDTO.builder()
                .name("Test template")
                .isShareable(true)
                .build();

        String gson = new Gson().toJson(templateDTO);

        mockMvc.perform(post("/api/v1/price-offer-template/create").
                contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateExistingPriceOfferTemplate() throws Exception {
        PriceOfferTemplateDTO templateDTO = PriceOfferTemplateDTO.builder()
                .name("Test template")
                .isShareable(true)
                .build();

        String gson = new Gson().toJson(templateDTO);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/price-offer-template/create")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(gson))
                .andExpect(status().isOk()).andReturn();

        String createResult = mvcResult.getResponse().getContentAsString();

        PriceOfferTemplateDTO requestBody = om.readValue(createResult, PriceOfferTemplateDTO.class);

        ObjectWriter ow = om.writer().withDefaultPrettyPrinter();
        mockMvc.perform(put("/api/v1/price-offer-template/save")
                .content(ow.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }
}
