package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import no.ding.pk.domain.User;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
class PriceOfferControllerTest {

    @Autowired
    private UserService userService;

    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        String salesEmployeeEmail = "Wolfgang@farris-bad.no";
        User salesEmployee = userService.findByEmail(salesEmployeeEmail);

        if(salesEmployee == null) {
            salesEmployee = User.builder()
                    .adId("ad-id-wegarijo-arha-rh-arha")
                    .jobTitle("Salgskonsulent")
                    .fullName("Wolfgang Amadeus Mozart")
                    .email(salesEmployeeEmail)
                    .associatedPlace("Larvik")
                    .department("Hvitsnippene")
                    .build();

            userService.save(salesEmployee, null);
        }

        String approverEmail = "alexander.brox@ngn.no";
        User approver = userService.findByEmail(approverEmail);

        if(approver == null) {
            approver = User.builder()
                    .adId("ad-ww-wegarijo-arha-rh-arha")
                    .associatedPlace("Oslo")
                    .email(approverEmail)
                    .department("Salg")
                    .fullName("Alexander Brox")
                    .name("Alexander")
                    .sureName("Brox")
                    .jobTitle("Markedskonsulent")
                    .build();

            userService.save(approver, null);
        }
    }

    @Test
    public void shouldPersistPriceOffer() throws Exception {
        setup();
        PriceOfferDTO priceOffer = createCompleteOfferDto();

        MvcResult result = mockMvc.perform(
                post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(priceOffer))
                        .with(jwt()
                                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldFailOnMissingSalesEmployeeIfGivenEmptyObject() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v2/price-offer/create").contentType(MediaType.APPLICATION_JSON)
                        .content("{}".getBytes())
                        .with(jwt()
                .authorities(List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL")))
                .jwt(jwt -> jwt.claim(StandardClaimNames.PREFERRED_USERNAME, "ch4mpy"))))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn();

        assertThat(result, notNullValue());
    }

    private PriceOfferDTO createCompleteOfferDto() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource("priceOfferWithZoneAndDiscount_V2.json")).getFile());

        assertThat(file.exists(), is(true));

        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, PriceOfferDTO.class);
    }
}