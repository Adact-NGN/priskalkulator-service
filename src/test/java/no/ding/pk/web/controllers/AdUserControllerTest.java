package no.ding.pk.web.controllers;

import com.azure.spring.cloud.autoconfigure.aad.properties.AadResourceServerProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.service.UserAzureAdService;
import no.ding.pk.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Bad ObjectMapper")
@ExtendWith(SpringExtension.class)
@WebMvcTest(AdUserControllerTest.class)
class AdUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserAzureAdService userAzureAdService;

    @MockBean
    private AadResourceServerProperties aadResourceServerProperties;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetSalesRoleAddedToUserObjectFetchedFromAD() throws Exception {

        String email = "kjetil.torvund.minde@ngn.no";

        User.UserBuilder userBuilder = User.builder("Kjetil", "Minde",  "Kjetil Torvund Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no");
        when(userAzureAdService.getUserByEmail(email))
                .thenReturn(userBuilder.build());

        SalesRole salesRole = SalesRole.builder("KN", 5, 5).build();
        User user = userBuilder.salesRole(salesRole).build();
        when(userService.findByEmail(email)).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(get("/api/ad/users/mail/" + email))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        ObjectReader reader = objectMapper.reader();

        User actualUser = reader.readValue(contentAsString, User.class);

        assertThat(actualUser.getSalesRole(), notNullValue());
    }

//    @Configuration
//    public static class ObjectMapperTestConfig {
//        @Bean
//        public ObjectMapper objectMapper() {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new Hibernate5Module());
//            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//            objectMapper.registerModule(new SimpleModule().addDeserializer(String.class, new AdUserControllerTest.ObjectMapperTestConfig.WhitespaceDeserializer()));
//
//            return objectMapper;
//        }
//
//        private static class WhitespaceDeserializer extends JsonDeserializer<String> {
//            @Override
//            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//                return p.getText().trim();
//            }
//        }
//    }
}