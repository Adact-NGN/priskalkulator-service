package no.ding.pk.web.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.service.UserService;
import no.ding.pk.web.dto.v1.web.client.PriceOfferDTO;
import no.ding.pk.web.dto.v1.web.client.SalesOfficeDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
public class PriceOfferControllerTest {
    
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
        
        ResponseEntity<String> responseEntity = this.restTemplate
        .postForEntity("http://localhost:" + serverPort + "/api/v1/price-offer/create", priceOffer, String.class);
        
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldMapJsonToEntity() throws IOException {
        PriceOfferDTO priceOfferDTO = createCompleteOfferDto();

        PriceOffer priceOffer = modelMapper.map(priceOfferDTO, PriceOffer.class);

        assertThat(priceOffer.getCustomerNumber(), equalTo(priceOfferDTO.getCustomerNumber()));
        assertThat(priceOffer.getCustomerName(), equalTo(priceOfferDTO.getCustomerName()));
        
        assertThat(priceOffer.getSalesOfficeList(), hasSize(1));
        SalesOffice salesOffice = priceOffer.getSalesOfficeList().get(0);
        SalesOfficeDTO salesOfficeDTO = priceOfferDTO.getSalesOfficeList().get(0);
        assertThat(salesOffice.getSalesOrg(), equalTo(salesOfficeDTO.getSalesOrg()));
        assertThat(salesOffice.getSalesOffice(), equalTo(salesOfficeDTO.getSalesOffice()));
        assertThat(salesOffice.getSalesOfficeName(), equalTo(salesOfficeDTO.getSalesOfficeName()));

        assertThat(salesOffice.getMaterialList(), hasSize(greaterThan(1)));
        List<PriceRow> materialList = salesOffice.getMaterialList();
        PriceRow priceRowMaterial = materialList.get(0);
        assertThat(priceRowMaterial.getStandardPrice(), equalTo(1199.0));
        assertThat(priceRowMaterial.getPriceIncMva(), equalTo(-1.0));
        assertThat(priceRowMaterial.getDiscountLevel(), equalTo(2));
        assertThat(priceRowMaterial.getDiscountLevelPrice(), equalTo(0.1));
        assertThat(priceRowMaterial.getMaterial(), notNullValue());
        
        Material material = priceRowMaterial.getMaterial();
        assertThat(material.getMaterialNumber(), equalTo("111101"));
        assertThat(material.getDesignation(), equalTo("Matavfall"));
        assertThat(material.getQuantumUnit(), equalTo("KG"));

        assertThat(salesOffice.getZoneList(), hasSize(1));
        Zone zone = salesOffice.getZoneList().get(0);
        assertThat(zone.getZoneId(), equalTo("2"));
        assertThat(zone.getPostalCode(), equalTo("3943"));
        assertThat(zone.getPostalName(), equalTo("PORSGRUNN"));
        assertThat(zone.getIsStandardZone(), is(true));


        assertThat(salesOffice.getTransportServiceList(), hasSize(0));
        assertThat(salesOffice.getRentalList(), hasSize(0));
        
        assertThat(priceOffer.getSalesEmployee().getId(), equalTo(priceOfferDTO.getSalesEmployee().getId()));
        assertThat(priceOffer.getSalesEmployee().getName(), equalTo(priceOfferDTO.getSalesEmployee().getName()));
        assertThat(priceOffer.getSalesEmployee().getPhoneNumber(), equalTo(priceOfferDTO.getSalesEmployee().getPhoneNumber()));
        assertThat(priceOffer.getSalesEmployee().getEmail(), equalTo(priceOfferDTO.getSalesEmployee().getEmail()));
    }

    private PriceOfferDTO createCompleteOfferDto() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource("priceOfferWithZoneAndDiscount.json")).getFile());

        assertThat(file.exists(), is(true));

        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, PriceOfferDTO.class);
    }

}
