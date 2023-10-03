package no.ding.pk.web.controllers.v1;

import com.google.gson.Gson;
import no.ding.pk.config.SecurityTestConfig;
import no.ding.pk.config.mapping.v1.ModelMapperConfig;
import no.ding.pk.web.dto.v2.web.client.offer.template.PriceOfferTemplateDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Import({SecurityTestConfig.class, ModelMapperConfig.class})
@WebMvcTest(controllers = PriceOfferTemplateController.class)
public class PriceOfferTemplateControllerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

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
}
