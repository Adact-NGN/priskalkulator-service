package no.ding.pk.repository.offer;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;

import no.ding.pk.config.H2TestConfig;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.Offer;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTemplate;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Terms;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.UserRepository;

import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

//@ContextConfiguration(classes = {H2TestConfig.class})
@DataJpaTest
@TestPropertySource("/h2-db.properties")
public class PriceOfferRepositoryTest {
    
    @Autowired
    private PriceOfferRepository repository;

    @Autowired
    private PriceOfferTemplateRepository offerTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateContext() {
        
        assertThat(repository, is(notNullValue()));
    }

    @Test
    public void shouldCreatePricingOfferAndPersistIt() {
        PriceOffer priceOffer = (PriceOffer) createCompleteOffer();

        priceOffer = repository.save(priceOffer);

        assertThat(priceOffer.getId(), notNullValue());
    }

    @Test
    public void shouldCreatePriceOfferTemplateAndPersistIt() {
        PriceOfferTemplate priceOfferTemplate = (PriceOfferTemplate) createCompleteOfferTemplate();

        priceOfferTemplate = offerTemplateRepository.save(priceOfferTemplate);

        assertThat(priceOfferTemplate.getId(), notNullValue());
    }

    private Offer createCompleteOfferTemplate() {
        User salesEmployee = createEmployee();

        User approver = User.builder()
        .adId("ad-ww-wegarijo-arha-rh-arha")
        .associatedPlace("Oslo")
        .email("alexander.brox@ngn.no")
        .department("Salg")
        .fullName("Alexander Brox")
        .name("Alexander")
        .sureName("Brox")
        .jobTitle("Markedskonsulent")
        .build();

        approver = userRepository.save(approver);

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
        .priceLevel(1)
        .priceLevelPrice(100.0)
        .amount(1)
        .priceIncMva(1125.0)
        .build();

        List<PriceRow> priceRowList = new ArrayList<>();
        priceRowList.add(priceRow);

        Zone zone = Zone.builder()
        .postalCode("1601")
        .postalName(null)
        .number("0000000001")
        .isStandardZone(true)
        .materialList(priceRowList)
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
        .priceLevel(1)
        .priceLevelPrice(56.0)
        .amount(1)
        .priceIncMva(2448.0)
        .build();

        List<PriceRow> wastePriceRowList = new ArrayList<>();
        wastePriceRowList.add(wastePriceRow);

        SalesOffice salesOffice = SalesOffice.builder()
        .city("FREDRIKSTAD")
        .name("Sarpsborg/Fredrikstad")
        .salesOffice("127")
        .salesOrg("100")
        .postalNumber("1601")
        .zoneList(zoneList)
        .materialList(wastePriceRowList)
        .build();

        List<SalesOffice> salesOfficeList = new ArrayList<>();
        salesOfficeList.add(salesOffice);

        PriceOfferTemplate priceOffer = PriceOfferTemplate.priceOfferTemplateBuilder()
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

    private Offer createCompleteOffer() {
        User salesEmployee = createEmployee();

        User approver = User.builder()
        .adId("ad-ww-wegarijo-arha-rh-arha")
        .associatedPlace("Oslo")
        .email("alexander.brox@ngn.no")
        .department("Salg")
        .fullName("Alexander Brox")
        .name("Alexander")
        .sureName("Brox")
        .jobTitle("Markedskonsulent")
        .build();

        approver = userRepository.save(approver);

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
        .priceLevel(1)
        .priceLevelPrice(100.0)
        .amount(1)
        .priceIncMva(1125.0)
        .build();

        List<PriceRow> priceRowList = new ArrayList<>();
        priceRowList.add(priceRow);

        Zone zone = Zone.builder()
        .postalCode("1601")
        .postalName(null)
        .number("0000000001")
        .isStandardZone(true)
        .materialList(priceRowList)
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
        .priceLevel(1)
        .priceLevelPrice(56.0)
        .amount(1)
        .priceIncMva(2448.0)
        .build();

        List<PriceRow> wastePriceRowList = new ArrayList<>();
        wastePriceRowList.add(wastePriceRow);

        SalesOffice salesOffice = SalesOffice.builder()
        .city("FREDRIKSTAD")
        .name("Sarpsborg/Fredrikstad")
        .salesOffice("127")
        .salesOrg("100")
        .postalNumber("1601")
        .zoneList(zoneList)
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

    private Offer createCompletePriceOfferTemplate() {
        User salesEmployee = createEmployee();

        return new PriceOfferTemplate();
    }

    private User createEmployee() {
        User salesEmployee = User.builder()
        .adId("ad-id-wegarijo-arha-rh-arha")
        .associatedPlace("Larvik")
        .email("Wolfgang@farris-bad.no")
        .department("Hvitsnippene")
        .fullName("Wolfgang Amadeus Mozart")
        .jobTitle("Komponist")
        .build();

        return userRepository.save(salesEmployee);
    }
}
