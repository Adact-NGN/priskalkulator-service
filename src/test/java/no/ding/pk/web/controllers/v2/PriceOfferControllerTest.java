package no.ding.pk.web.controllers.v2;

import com.azure.spring.cloud.autoconfigure.aad.properties.AadResourceServerProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PriceOfferController.class)
public class PriceOfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AadResourceServerProperties aadResourceServerProperties;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    public void shouldThrowExceptionWhenNonValidPriceOfferStatusIsGiven() throws Exception {

        mockMvc.perform(put("/api/v2/price-offer/status/1")
                .requestAttr("status", "NOT_A_VALID_STATYS"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Configuration
    public static class PriceOfferControllerTestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Hibernate5Module());
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            objectMapper.registerModule(new SimpleModule().addDeserializer(String.class, new PriceOfferControllerTestConfig.WhitespaceDeserializer()));

            return objectMapper;
        }

        private static class WhitespaceDeserializer extends JsonDeserializer<String> {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return p.getText().trim();
            }
        }
    }
}
