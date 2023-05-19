package no.ding.pk.repository.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.enums.TermsTypes;
import org.apache.commons.io.IOUtils;
import org.aspectj.weaver.tools.MatchingContext;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
public class PriceOfferRepositoryTest {

    @Autowired
    private PriceOfferRepository repository;

    @Autowired
    private PriceOfferTemplateRepository offerTemplateRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    public void shouldGetTrueWhenOfferIsDelete() {
        PriceOffer priceOffer = (PriceOffer) createCompleteOffer();

        priceOffer = repository.save(priceOffer);

        priceOffer.setDeleted(true);

        priceOffer = repository.save(priceOffer);

        repository.existsByIdAndDeleted(priceOffer.getId());
    }

    @Test
    public void shouldFindAllByApproverIdAndNeedsApprovalIsTrue() {
        User salesAndApprover = User.builder()
                .adId("183a1b82-d795-47d1-94a1-96f6aa5a268a")
                .orgNr("100")
                .orgName("Norsk Gjenvinning")
                .sureName("Nilsen")
                .name("Thomas")
                .username("thomas.nilsen@ngn.no")
                .usernameAlias("vh3180")
                .jobTitle("Utvikler")
                .fullName("Thomas Nilsen")
                .powerOfAttorneyFA(2)
                .powerOfAttorneyOA(2)
                .build();

        salesAndApprover = userRepository.save(salesAndApprover);

        createCompleteOfferDtoList(salesAndApprover);

        List<PriceOffer> actual = repository.findAllByApproverIdAndNeedsApprovalIsTrue(salesAndApprover.getId());

        assertThat(actual, hasSize(greaterThan(0)));
    }

    private void createCompleteOfferDtoList(User salesAndApproval) {
        Material material = Material.builder()
                .materialNumber("70120015")
                .designation("Ikke refunderbar spillolje,Småemb")
                .materialTypeDescription("Farlig Avfallsmateriale")
                .quantumUnit("KG")
                .build();

        List<PriceRow> materialList = List.of(PriceRow.builder()
                .material(material)
                .showPriceInOffer(true)
                .manualPrice(10.0)
                .standardPrice(17180.0)
                .needsApproval(true)
                .categoryId("00310")
                .categoryDescription("Farlig avfall")
                .subCategoryId("0031000100")
                .subCategoryDescription("FA Diverse")
                .build()
        );
        SalesOffice bergen = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("134")
                .salesOfficeName("Bergen")
                .city("SANDSLI")
                .materialList(materialList)
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("2756")
                .customerName("HydroscandGjøviKongsvKr.SundÅgotnes")
                .salesEmployee(salesAndApproval)
                .approver(salesAndApproval)
                .priceOfferTerms(PriceOfferTerms.builder().additionForAdminFee(true).contractTerm(TermsTypes.NGPriceTerms.name()).build())
                .salesOfficeList(List.of(bergen))
                .needsApproval(true)
                .build();

        repository.save(priceOffer);
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

        PriceOfferTerms priceOfferTerms = PriceOfferTerms.builder()
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
                .build();

        priceOffer.setCustomerTerms(priceOfferTerms);

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

        PriceOfferTerms customerTerms = PriceOfferTerms.builder()
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
                .zoneList(zoneList)
                .materialList(wastePriceRowList)
                .build();

        List<SalesOffice> salesOfficeList = new ArrayList<>();
        salesOfficeList.add(salesOffice);

        return PriceOffer.priceOfferBuilder()
                .customerNumber("5162")
                .salesOfficeList(salesOfficeList)
                .salesEmployee(salesEmployee)
                .needsApproval(true)
                .approver(approver)
                .approved(false)
                .priceOfferTerms(customerTerms)
                .build();
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
