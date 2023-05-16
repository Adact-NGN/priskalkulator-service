package no.ding.pk.repository.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.Offer;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceOfferTemplate;
import no.ding.pk.domain.offer.PriceOfferTerms;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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

    @Disabled
    @Test
    public void shouldGetTrueWhenOfferIsDelete() {
        PriceOffer priceOffer = (PriceOffer) createCompleteOffer();

        priceOffer = repository.save(priceOffer);

        priceOffer.setDeleted(true);

        priceOffer = repository.save(priceOffer);

        repository.existsByIdAndDeleted(priceOffer.getId());
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
