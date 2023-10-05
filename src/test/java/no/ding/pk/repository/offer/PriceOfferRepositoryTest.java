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

import static no.ding.pk.repository.specifications.ApprovalSpecifications.withApproverId;
import static no.ding.pk.repository.specifications.ApprovalSpecifications.withPriceOfferStatus;
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

        List<PriceOffer> actual = repository.findAll(Specification.where(withApproverId(salesAndApprover.getId())).and(withPriceOfferStatus(PriceOfferStatus.PENDING.getStatus())));

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetAllPriceOfferReadyForBoReport() {
        PriceOffer build = PriceOffer.priceOfferBuilder().build();

        assertThat(build, notNullValue());
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
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        repository.save(priceOffer);
    }

    private PriceOfferTemplate createCompleteOfferTemplate() {
        User salesEmployee = createEmployee();

        User sharedWithUser = User.builder()
                .adId("ad-ww-wegarijo-arha-rh-arha")
                .associatedPlace("Oslo")
                .email("alexander.brox@ngn.no")
                .department("Salg")
                .fullName("Alexander Brox")
                .name("Alexander")
                .sureName("Brox")
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
                .discountLevelPct(0.02)
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
                .salesOffice("127")
                .salesOrg("100")
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
