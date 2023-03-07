package no.ding.pk.web.controllers.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.User;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
class PriceOfferControllerTest {
    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    private User salesEmployee;
    private User approver;

    public void setup() {

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

            salesEmployee = userService.save(salesEmployee, null);
        }
        this.salesEmployee = userService.findByEmail(salesEmployeeEmail);

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

            approver = userService.save(approver, null);
        }


        this.approver = approver;
    }

    @Test
    public void shouldPersistPriceOffer() throws Exception {
        setup();
        PriceOfferDTO priceOffer = createCompleteOfferDto();

        ResponseEntity<PriceOfferDTO> responseEntity = this.restTemplate
                .postForEntity("http://localhost:" + serverPort + "/api/v2/price-offer/create", priceOffer, PriceOfferDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
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