package no.ding.pk.repository.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.domain.offer.template.TemplateMaterial;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.enums.TermsTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static no.ding.pk.repository.specifications.PriceOfferSpecifications.*;
import static no.ding.pk.repository.specifications.PriceOfferSpecifications.withPriceOfferStatusInList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@TestPropertySource("classpath:h2-db.properties")
public class PriceOfferRepositoryTest {

    @Autowired
    private PriceOfferRepository repository;

    @Autowired
    private PriceOfferTemplateRepository offerTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Test
    public void shouldCreateContext() {

        assertThat(repository, is(notNullValue()));
    }

    @Test
    public void shouldCreatePricingOfferAndPersistIt() {
        PriceOffer priceOffer = (PriceOffer) createCompleteOffer();

        priceOffer = repository.save(priceOffer);

        assertThat(priceOffer.getId(), notNullValue());
        List<Zone> zoneList = priceOffer.getSalesOfficeList().get(0).getZoneList();
        assertThat(zoneList, hasSize(3));
        assertThat(zoneList.get(0).getPriceRows(), hasSize(3));
        assertThat(zoneList.get(1).getPriceRows(), hasSize(3));
        assertThat(zoneList.get(2).getPriceRows(), hasSize(3));
    }

    @Test
    public void shouldCreatePriceOfferTemplateAndPersistIt() {
        PriceOfferTemplate priceOfferTemplate = createCompleteOfferTemplate();

        priceOfferTemplate = offerTemplateRepository.save(priceOfferTemplate);

        assertThat(priceOfferTemplate.getId(), notNullValue());
        assertThat(priceOfferTemplate.getSharedWith(), hasSize(greaterThan(0)));
        assertThat(priceOfferTemplate.getMaterials(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetTrueWhenOfferIsDelete() {
        PriceOffer priceOffer = (PriceOffer) createCompleteOffer();

        priceOffer = repository.save(priceOffer);

//        priceOffer.setDeleted(true);
        PriceOffer updatedPriceOffer = repository.findById(priceOffer.getId()).orElse(null);

        assertThat(updatedPriceOffer, notNullValue());

        updatedPriceOffer = repository.save(updatedPriceOffer);

        repository.existsByIdAndDeleted(priceOffer.getId());
    }

    @Test
    public void shouldFindAllByApproverIdAndNeedsApprovalIsTrue() {
        User salesAndApprover = User.builder("Thomas", "Nilsen", "Thomas Nilsen",
                        "thomas.nilsen@ngn.no", "thomas.nilsen@ngn.no")
                .adId("183a1b82-d795-47d1-94a1-96f6aa5a268a")
                .orgNr("100")
                .orgName("Norsk Gjenvinning")
                .usernameAlias("vh3180")
                .jobTitle("Utvikler")
                .fullName("Thomas Nilsen")
                .powerOfAttorneyFA(2)
                .powerOfAttorneyOA(2)
                .build();

        salesAndApprover = userRepository.save(salesAndApprover);

        createCompleteOfferDtoList(salesAndApprover);

        List<PriceOffer> actual = repository.findAll(Specification.where(withApproverId(salesAndApprover.getId())).and(withPriceOfferStatus(PriceOfferStatus.PENDING.getStatus())));

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetAllPriceOfferReadyForBoReport() {
        PriceOffer build = PriceOffer.priceOfferBuilder().build();

        assertThat(build, notNullValue());
    }

    @Test
    public void shouldUpdatePriceOfferStatus() {
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(User.builder("Test", "Testesen", "Test Testesen", "test.testesen@testing.no", "test.testesen@testing.no")
                        .build())
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        priceOffer = repository.save(priceOffer);

        assertThat(priceOffer.getId(), notNullValue());

        priceOffer.setPriceOfferStatus(PriceOfferStatus.DRAFT.getStatus());

        priceOffer = repository.save(priceOffer);

        PriceOffer actualPriceOffer = repository.findById(priceOffer.getId()).orElse(null);

        assertThat(actualPriceOffer.getPriceOfferStatus(), notNullValue());
        assertThat(actualPriceOffer.getPriceOfferStatus(), equalTo(PriceOfferStatus.DRAFT.getStatus()));
    }

    @Test
    public void shouldReturnAllPriceOffersInSalesOfficeNumberList() {

        SalesOffice salesOffice100 = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("100")
                .build();

        SalesOffice salesOffice104 = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("104")
                .build();

        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(salesOffice100, salesOffice104))
                .build();

        repository.save(priceOffer);

        List<String> salesOffices = List.of("100", "104");
        List<PriceOffer> actual = repository.findAllBySalesOfficeInList(salesOffices);

        assertThat(actual, hasSize(1));
    }

    @Test
    public void shouldReturnNoPriceOffersInSalesOfficeNumberList() {

        SalesOffice salesOffice100 = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("100")
                .build();

        SalesOffice salesOffice104 = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("104")
                .build();

        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(salesOffice100, salesOffice104))
                .build();

        repository.save(priceOffer);

        List<String> salesOffices = List.of("109", "110");
        List<PriceOffer> actual = repository.findAllBySalesOfficeInList(salesOffices);

        assertThat(actual, hasSize(0));
    }

    @Test
    public void shouldReturnMultiplePriceOffersInSalesOfficeNumberList() {

        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerName("First customer")
                .customerNumber("123456789")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("100")
                                .build(),
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .build()))
                .build();

        User otherSalesEmployee = User.builder("Eirik", "Flaa", "Eirik Flaa", "Eirik.Flaa@ngn.no", "Eirik.Flaa@ngn.no").build();

        PriceOffer otherPriceOffer = PriceOffer.priceOfferBuilder()
                .customerName("Other customer")
                .customerNumber("987654321")
                .salesEmployee(otherSalesEmployee)
                .salesOfficeList(List.of(
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("100")
                                .build(),
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .build()))
                .build();

        repository.save(priceOffer);
        repository.save(otherPriceOffer);

        List<String> salesOffices = List.of("100");
        List<PriceOffer> actual = repository.findAllBySalesOfficeInList(salesOffices);

        assertThat(actual, hasSize(2));
    }

    @Test
    public void shouldReturnSinglePriceOffersInSalesOfficeNumberList() {

        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus("PENDING")
                .customerName("First customer")
                .customerNumber("123456789")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("100")
                                .build(),
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .build()))
                .build();

        User otherSalesEmployee = User.builder("Eirik", "Flaa", "Eirik Flaa", "Eirik.Flaa@ngn.no", "Eirik.Flaa@ngn.no").build();

        PriceOffer otherPriceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus("ACTIVATED")
                .customerName("Other customer")
                .customerNumber("987654321")
                .salesEmployee(otherSalesEmployee)
                .salesOfficeList(List.of(
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("102")
                                .build(),
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .build()))
                .build();

        repository.save(priceOffer);
        repository.save(otherPriceOffer);

        List<String> salesOffices = List.of("104");
        List<PriceOffer> actual = repository.findAll(Specification.where(withSalesOfficeInList(salesOffices)).and(withPriceOfferStatusInList(List.of("PENDING"))));

        assertThat(actual, hasSize(1));
    }

    @Test
    public void shouldReturnAllPriceOffersWhenStatusIsNotGiven() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus("PENDING")
                .customerName("First customer")
                .customerNumber("123456789")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("100")
                                .build(),
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .build()))
                .build();

        User otherSalesEmployee = User.builder("Eirik", "Flaa", "Eirik Flaa", "Eirik.Flaa@ngn.no", "Eirik.Flaa@ngn.no").build();

        PriceOffer otherPriceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus("ACTIVATED")
                .customerName("Other customer")
                .customerNumber("987654321")
                .salesEmployee(otherSalesEmployee)
                .salesOfficeList(List.of(
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("102")
                                .build(),
                        SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .build()))
                .build();

        repository.save(priceOffer);
        repository.save(otherPriceOffer);

        List<String> salesOffices = List.of("100", "102", "104");
        List<PriceOffer> actual = repository.findAll(Specification.where(distinct()).and(withSalesOfficeInList(salesOffices)).and(withPriceOfferStatusInList(null)));

        assertThat(actual, hasSize(2));
    }

    private void createCompleteOfferDtoList(User salesAndApproval) {

        Material material = Material.builder()
                .materialNumber("70120015")
                .designation("Ikke refunderbar spillolje,Småemb")
                .materialTypeDescription("Farlig Avfallsmateriale")
                .quantumUnit("KG")
                .build();

        material = materialRepository.save(material);

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
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        repository.save(priceOffer);
    }

    private PriceOfferTemplate createCompleteOfferTemplate() {
        User salesEmployee = createEmployee();

        User sharedWithUser = User.builder("Alexander", "Brox", "Alexander Brox",
                        "alexander.brox@ngn.no", "alexander.brox@ngn.no")
                .adId("ad-ww-wegarijo-arha-rh-arha")
                .associatedPlace("Oslo")
                .department("Salg")
                .jobTitle("Markedskonsulent")
                .build();

        sharedWithUser = userRepository.save(sharedWithUser);

        TemplateMaterial material = TemplateMaterial.builder()
                .material("50101")
                .build();

        TemplateMaterial waste = TemplateMaterial.builder()
                .material("119901")
                .build();


        return PriceOfferTemplate.builder()
                .name("Test template")
                .author(salesEmployee)
                .materials(List.of(material, waste))
                .isShareable(true)
                .sharedWith(List.of(sharedWithUser))
                .build();
    }

    private Offer createCompleteOffer() {
        User salesEmployee = createEmployee();

        User approver = User.builder("Alexander", "Brox", "Alexander Brox",
                        "alexander.brox@ngn.no", "alexander.brox@ngn.no")
                .adId("ad-ww-wegarijo-arha-rh-arha")
                .associatedPlace("Oslo")
                .department("Salg")
                .jobTitle("Markedskonsulent")
                .build();

        approver = userRepository.save(approver);

        PriceOfferTerms customerTerms = PriceOfferTerms.builder()
                .agreementStartDate(new Date())
                .contractTerm("Generelle vilkår")
                .additionForAdminFee(false)
                .build();

        String salesOfficeNumber = "127";
        String salesOrg = "100";

        Zone zoneOne = createZone("0000000001", true, salesOrg, salesOfficeNumber);
        Zone zoneTwo = createZone("0000000002", false, salesOrg, salesOfficeNumber);
        Zone zoneThree = createZone("0000000003", false, salesOrg, salesOfficeNumber);

        List<Zone> zoneList = new ArrayList<>();
        zoneList.add(zoneOne);
        zoneList.add(zoneTwo);
        zoneList.add(zoneThree);



        MaterialPrice wastePrice = MaterialPrice.builder(salesOrg, salesOfficeNumber, "119901", null, "01")
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
                .discountLevelPct(0.02)
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
                .salesOffice(salesOfficeNumber)
                .salesOrg(salesOrg)
                .postalNumber("1601")
                .zoneList(zoneList)
                .materialList(wastePriceRowList)
                .build();

        List<SalesOffice> salesOfficeList = new ArrayList<>();
        salesOfficeList.add(salesOffice);

        ContactPerson contactPerson = ContactPerson.builder()
                .firstName("Test")
                .lastName("Testesen")
                .emailAddress("test.testesen@testing.com")
                .mobileNumber("123456789")
                .build();

        List<ContactPerson> contactPersonList = new LinkedList<>();
        contactPersonList.add(contactPerson);
        contactPersonList.add(ContactPerson.builder()
                .firstName("Testa")
                .lastName("Testesen")
                .emailAddress("testa.testesen@testing.com")
                .mobileNumber("987654321")
                .build());
        return PriceOffer.priceOfferBuilder()
                .customerNumber("5162")
                .contactPersonList(contactPersonList)
                .salesOfficeList(salesOfficeList)
                .salesEmployee(salesEmployee)
                .approver(approver)
                .priceOfferTerms(customerTerms)
                .build();
    }

    private static Zone createZone(String zoneId, boolean isStandardZone, String salesOrg, String salesOffice) {
        String formattedZoneId = String.format("0%d", Integer.valueOf(zoneId));
        MaterialPrice materialPrice = MaterialPrice.builder(salesOrg, salesOffice, "50101", null, formattedZoneId)
                .standardPrice(1131.0)
                .build();

        Material liftPlacementMaterial = Material.builder()
                .materialNumber("50101")
                .designation("Lift - Utsett")
                .pricingUnit(1)
                .quantumUnit("ST")
                .materialStandardPrice(materialPrice)
                .build();

        PriceRow priceRow = PriceRow.builder()
                .customerPrice(1000.0)
                .discountLevelPct(0.02)
                .material(liftPlacementMaterial)
                .showPriceInOffer(true)
                .manualPrice(900.0)
                .discountLevel(1)
                .discountLevelPrice(100.0)
                .amount(1)
                .priceIncMva(1125.0)
                .build();

        MaterialPrice liftExchangePrice = MaterialPrice.builder("100", "100", "50102", null, formattedZoneId)
                .standardPrice(1468.0)
                .build();
        Material liftExchange = Material.builder()
                .materialNumber("50102")
                .designation("Lift - Utbytte")
                .pricingUnit(1)
                .quantumUnit("ST")
                .materialStandardPrice(liftExchangePrice)
                .build();

        PriceRow liftExchangePriceRow = PriceRow.builder()
                .customerPrice(1000.0)
                .discountLevelPct(0.02)
                .material(liftExchange)
                .showPriceInOffer(true)
                .manualPrice(900.0)
                .discountLevel(1)
                .discountLevelPrice(100.0)
                .amount(1)
                .priceIncMva(1125.0)
                .build();

        MaterialPrice liftEmptyingPrice = MaterialPrice.builder("100", "100", "50103", null, formattedZoneId)
                .standardPrice(1131.0)
                .build();
        Material liftEmptying = Material.builder()
                .materialNumber("50103")
                .designation("Lift - Tømming")
                .pricingUnit(1)
                .quantumUnit("ST")
                .materialStandardPrice(liftEmptyingPrice)
                .build();

        PriceRow liftEmptyingPriceRow = PriceRow.builder()
                .customerPrice(1000.0)
                .discountLevelPct(0.02)
                .material(liftExchange)
                .showPriceInOffer(true)
                .manualPrice(900.0)
                .discountLevel(1)
                .discountLevelPrice(100.0)
                .amount(1)
                .priceIncMva(1125.0)
                .build();

        List<PriceRow> priceRowList = new ArrayList<>();
        priceRowList.add(priceRow);
        priceRowList.add(liftEmptyingPriceRow);
        priceRowList.add(liftExchangePriceRow);

        return Zone.builder()
                .postalCode("1601")
                .postalName(null)
                .zoneId(zoneId)
                .isStandardZone(isStandardZone)
                .priceRows(priceRowList)
                .build();
    }

    private User createEmployee() {
        User salesEmployee = User.builder("Wolfgang Amadeus", "Mozart", "Wolfgang Amadeus Mozart",
                        "Wolfgang@farris-bad.no", "Wolfgang@farris-bad.no")
                .adId("ad-id-wegarijo-arha-rh-arha")
                .associatedPlace("Larvik")
                .department("Hvitsnippene")
                .jobTitle("Komponist")
                .build();

        return userRepository.save(salesEmployee);
    }
}
