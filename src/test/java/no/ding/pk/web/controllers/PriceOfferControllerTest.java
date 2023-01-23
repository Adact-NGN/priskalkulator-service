package no.ding.pk.web.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.Offer;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Terms;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.service.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
public class PriceOfferControllerTest {
    
    @LocalServerPort
    private int serverPort;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserService userService;
    
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
        PriceOffer priceOffer = (PriceOffer) createCompleteOffer();
        
        ResponseEntity<String> responseEntity = this.restTemplate
        .postForEntity("http://localhost:" + serverPort + "/api/v1/price-offer/create", priceOffer, String.class);
        
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }
    
    private Offer createCompleteOffer() {
        Terms customerTerms = Terms.builder()
        .agreementStartDate(new Date())
        .contractTerm("Generelle vilkår")
        .additionForAdminFee(false)
        .build();
        
        MaterialPrice materialPrice = MaterialPrice.builder()
        .materialNumber("50101")
        .standardPrice(1131.0)
        .build();
        
        Material material = Material.builder()
        .materialNumber("50101")
        .designation("Lift - Utsett")
        .pricingUnit(1)
        .quantumUnit("ST")
        .materialStandardPrice(materialPrice)
        .build();
        
        PriceRow priceRow = PriceRow.builder()
        .customerPrice(1000.0)
        .discountPct(0.02)
        .material(material)
        .showPriceInOffer(true)
        .manualPrice(900.0)
        .discountLevel(1)
        .discountLevelPrice(100.0)
        .amount(1)
        .priceIncMva(1125.0)
        .build();
        
        List<PriceRow> priceRowList = new ArrayList<>();
        priceRowList.add(priceRow);
        
        Zone zone = Zone.builder()
        .postalCode("1601")
        .postalName(null)
        .zoneId("0000000001")
        .isStandardZone(true)
        .priceRows(priceRowList)
        .build();
        
        List<Zone> zoneList = new ArrayList<>();
        zoneList.add(zone);
        
        MaterialPrice wastePrice = MaterialPrice.builder()
        .materialNumber("119901")
        .standardPrice(2456.00)
        .build();
        
        Material waste = Material.builder()
        .materialNumber("119901")
        .designation("Restavfall")
        .pricingUnit(1000)
        .quantumUnit("KG")
        .materialStandardPrice(wastePrice)
        .build();
        
        PriceRow wastePriceRow = PriceRow.builder()
        .customerPrice(2456.0)
        .discountPct(0.02)
        .material(waste)
        .showPriceInOffer(true)
        .manualPrice(2400.0)
        .discountLevel(1)
        .discountLevelPrice(56.0)
        .amount(1)
        .priceIncMva(2448.0)
        .build();
        
        List<PriceRow> wastePriceRowList = new ArrayList<>();
        wastePriceRowList.add(wastePriceRow);
        
        SalesOffice salesOffice = SalesOffice.builder()
        .city("FREDRIKSTAD")
        .salesOfficeName("Sarpsborg/Fredrikstad")
        .salesOffice("127")
        .salesOrg("100")
        .postalNumber("1601")
        .zones(zoneList)
        .materialList(wastePriceRowList)
        .build();
        
        List<SalesOffice> salesOfficeList = new ArrayList<>();
        salesOfficeList.add(salesOffice);
        
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
        .customerNumber("5162")
        .salesOfficeList(salesOfficeList)
        .salesEmployee(salesEmployee)
        .needsApproval(true)
        .approver(approver)
        .approved(false)
        .customerTerms(customerTerms)
        .build();
        
        return priceOffer;
    }
}
