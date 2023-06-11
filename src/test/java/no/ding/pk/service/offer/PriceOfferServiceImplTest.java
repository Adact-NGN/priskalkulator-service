package no.ding.pk.service.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.enums.TermsTypes;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
class PriceOfferServiceImplTest {
    @Autowired
    private PriceOfferService service;

    @Autowired
    private UserService userService;

    @Autowired
    private SalesRoleService salesRoleService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SalesOfficePowerOfAttorneyService salesOfficePowerOfAttorneyService;

    @Autowired
    private CustomerTermsService customerTermsService;

    @BeforeEach
    public void setup() {

        persistSalesRoles();
        createMaterial();

        User alex = userService.findByEmail("alexander.brox@ngn.no");

        if(alex == null) {
            alex = userService.save(User.builder()
                            .id(39L)
                            .adId("e2f1963a-072a-4414-8a0b-6a3aa6988e0c")
                            .name("Alexander")
                            .sureName("Brox")
                            .fullName("Alexander Brox")
                            .orgNr("100")
                            .resourceNr("63874")
                            .associatedPlace("Oslo")
                            .phoneNumber("95838638")
                            .email("alexander.brox@ngn.no")
                            .jobTitle("Markedskonsulent")
                            .powerOfAttorneyOA(5)
                            .powerOfAttorneyFA(3)
                            .build()
                    , null);
        }

        SalesRole knSalesRole = salesRoleService.findSalesRoleByRoleName("KN");
        knSalesRole.addUser(alex);
        salesRoleService.save(knSalesRole);

        User eirik = userService.findByEmail("Eirik.Flaa@ngn.no");

        if(eirik == null) {
            userService.save(User.builder()
                    .name("Eirik")
                    .sureName("Flaa")
                    .orgNr("100")
                    .associatedPlace("Larvik")
                    .email("Eirik.Flaa@ngn.no")
                    .jobTitle("Prosjektleder")
                    .powerOfAttorneyFA(5)
                    .powerOfAttorneyOA(5)
                    .build(),
                    null
            );
        }

        User kjetil = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        if(kjetil == null) {
            userService.save(User.builder()
                            .name("Kjetil")
                            .sureName("Minde")
                            .orgNr("100")
                            .associatedPlace("Larvik")
                            .email("kjetil.torvund.minde@ngn.no")
                            .jobTitle("Fullstack utvikler")
                            .powerOfAttorneyFA(5)
                            .powerOfAttorneyOA(5)
                            .build(),
                    null
            );
        }

        User salesEmployee = userService.findByEmail("Wolfgang@farris-bad.no");

        if(salesEmployee == null) {
            salesEmployee = User.builder()
                    .adId("ad-id-wegarijo-arha-rh-arha")
                    .jobTitle("Komponist")
                    .fullName("Wolfgang Amadeus Mozart")
                    .email("Wolfgang@farris-bad.no")
                    .associatedPlace("Larvik")
                    .department("Hvitsnippene")
                    .build();

            salesEmployee = userService.save(salesEmployee, null);
        }

        SalesRole saSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        saSalesRole.addUser(salesEmployee);
        salesRoleService.save(saSalesRole);
    }

    @Test
    public void shouldPersistPriceOffer() throws JsonProcessingException {
        PriceOfferTerms priceOfferTerms = PriceOfferTerms.builder()
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(new Date())
                .build();


        MaterialPrice residualWasteMaterialStdPrice = MaterialPrice.builder()
                .materialNumber("119901")
                .standardPrice(2456.0)
                .build();
        Material residualWasteMaterial = Material.builder()
                .materialNumber("119901")
                .designation("Restavfall")
                .materialGroupDesignation("Bl. næringsavfall")
                .pricingUnit(1000)
                .quantumUnit("KG")
                .materialStandardPrice(residualWasteMaterialStdPrice)
                .build();
        PriceRow priceRow = PriceRow.builder()
                .customerPrice(2456.0)
                .discountPct(0.02)
                .showPriceInOffer(true)
                .manualPrice(2400.0)
                .discountLevel(1)
                .discountLevelPrice(56.0)
                .amount(1)
                .priceIncMva(2448.0)
                .material(residualWasteMaterial)
                .build();
        List<PriceRow> materialList = List.of(priceRow);

        MaterialPrice zoneMaterialStandardPrice = MaterialPrice.builder()
                .materialNumber("50101")
                .standardPrice(1131.0)
                .build();
        Material zoneMaterial = Material.builder()
                .materialNumber("50101")
                .designation("Lift - Utsett")
                .materialGroupDesignation("Tjeneste")
                .pricingUnit(1)
                .quantumUnit("ST")
                .materialStandardPrice(zoneMaterialStandardPrice)
                .build();
        PriceRow zonePriceRow = PriceRow.builder()
                .customerPrice(1000.0)
                .discountPct(0.02)
                .showPriceInOffer(true)
                .manualPrice(900.0)
                .discountLevel(1)
                .discountLevelPrice(100.0)
                .amount(1)
                .priceIncMva(1125.0)
                .material(zoneMaterial)
                .build();
        List<PriceRow> zoneMaterialList = List.of(zonePriceRow);
        Zone zone = Zone.builder()
                .zoneId("0000000001")
                .postalCode("1601")
                .postalName("FREDRIKSTAD")
                .isStandardZone(true)
                .priceRows(zoneMaterialList)
                .build();
        List<Zone> zoneList = List.of(zone);
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("127")
                .salesOfficeName("Sarpsborg/Fredrikstad")
                .postalNumber("1601")
                .city("FREDRIKSTAD")
                .materialList(materialList)
                .zoneList(zoneList)
                .build();

        User salesEmployee = userService.findByEmail("alexander.brox@ngn.no");

        List<SalesOffice> salesOfficeList = List.of(salesOffice);
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("5162")
                .customerName("Europris Telem Notodden")
                .needsApproval(true)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .salesEmployee(salesEmployee)
                .salesOfficeList(salesOfficeList)
                .build();

        priceOffer.setNeedsApproval(true);

        priceOffer.setCustomerTerms(priceOfferTerms);

        ObjectMapper objectMapper = new ObjectMapper();
        PriceOfferTerms priceOfferTerms2 = objectMapper.readValue(objectMapper.writeValueAsString(priceOfferTerms), PriceOfferTerms.class);

        SalesOffice salesOffice2 = objectMapper.readValue(objectMapper.writeValueAsString(salesOffice), SalesOffice.class);

        PriceOffer priceOffer2 = PriceOffer.priceOfferBuilder()
                .customerNumber("327342")
                .customerName("Follo Ren IKS")
                .needsApproval(false)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(salesOffice2))
                .build();

        priceOffer2.setCustomerTerms(priceOfferTerms2);

        assertThat(salesEmployee.getId(), notNullValue());

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer, notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));

        priceOffer2 = service.save(priceOffer2);

        assertThat(priceOffer2, notNullValue());
        assertThat(priceOffer2.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer2.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer2.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldSetFaApproverWhenOnlyDangerousWastNeedsApproval() {
        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder()
                .salesOffice(104)
                .salesOfficeName("Skien")
                .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                .dangerousWasteHolder(dangerousWasteHolder)
                .build();
        salesOfficePowerOfAttorneyService.save(powerOfAttorney);


        Material material = createDangerousMaterial();
        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .standardPrice(16566.00)
                .discountLevel(3)
                .needsApproval(true)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOfficeName("Skien")
                .salesOffice("104")
                .materialList(List.of(priceRow))
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(dangerousWasteHolder)
                .salesOfficeList(List.of(salesOffice))
                .needsApproval(true)
                .build();

        PriceOffer actual = service.save(priceOffer);

        assertThat(actual.getApprover(), notNullValue());
        assertThat(actual.getApprover(), equalTo(dangerousWasteHolder));
    }

    @Test
    public void shouldSetOrdinaryLvlOneApproverWhenDangerousWastAndOrdinaryMaterialNeedsApproval() {
        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder()
                .salesOffice(104)
                .salesOfficeName("Skien")
                .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                .dangerousWasteHolder(dangerousWasteHolder)
                .build();
        salesOfficePowerOfAttorneyService.save(powerOfAttorney);


        Material ordinaryMaterial = createOrdinaryMaterial();
        PriceRow ordinaryWastePriceRow = PriceRow.builder()
                .material(ordinaryMaterial)
                .standardPrice(170.00)
                .discountLevel(3)
                .needsApproval(true)
                .build();

        Material dangerousMaterial = createDangerousMaterial();
        PriceRow priceRow = PriceRow.builder()
                .material(dangerousMaterial)
                .standardPrice(16566.00)
                .discountLevel(3)
                .needsApproval(true)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOfficeName("Skien")
                .salesOffice("104")
                .materialList(List.of(priceRow, ordinaryWastePriceRow))
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(dangerousWasteHolder)
                .salesOfficeList(List.of(salesOffice))
                .needsApproval(true)
                .build();

        PriceOffer actual = service.save(priceOffer);

        assertThat(actual.getApprover(), notNullValue());
        assertThat(actual.getApprover(), equalTo(ordinaryWasteHolder));
    }

    @Test
    public void shouldSetOrdinaryLvlTwoWhenDiscountLevelIsAtItsHighest() {
        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder()
                .salesOffice(104)
                .salesOfficeName("Skien")
                .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                .dangerousWasteHolder(dangerousWasteHolder)
                .build();
        salesOfficePowerOfAttorneyService.save(powerOfAttorney);

        Material ordinaryMaterial = createOrdinaryMaterial();
        PriceRow ordinaryWastePriceRow = PriceRow.builder()
                .material(ordinaryMaterial)
                .standardPrice(170.00)
                .discountLevel(6)
                .needsApproval(true)
                .build();

        Material dangerousMaterial = createDangerousMaterial();
        PriceRow priceRow = PriceRow.builder()
                .material(dangerousMaterial)
                .standardPrice(16566.00)
                .discountLevel(3)
                .needsApproval(true)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOfficeName("Skien")
                .salesOffice("104")
                .materialList(List.of(priceRow, ordinaryWastePriceRow))
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(dangerousWasteHolder)
                .salesOfficeList(List.of(salesOffice))
                .needsApproval(true)
                .build();

        PriceOffer actual = service.save(priceOffer);

        assertThat(actual.getApprover(), notNullValue());
        assertThat(actual.getApprover(), equalTo(ordinaryWasteHolderLvl2));
    }

    @Test
    public void shouldEndExistingCustomerTermsAndAddNewWhenNewPriceOfferIsActivated() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        User salesEmployee = userService.findByEmail("alexander.brox@ngn.no");

        CustomerTerms oldCustomerTerms = CustomerTerms.builder()
                .customerNumber("169239")
                .customerName("Monica")
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(currentDateTime.minusYears(1).toDate())
                .salesEmployee(salesEmployee.getEmail())
                .salesOrg("100")
                .salesOffice("100")
                .build();

        customerTermsService.save("100", "169239", oldCustomerTerms);

        Material ordinaryMaterial = createOrdinaryMaterial();
        PriceRow ordinaryWastePriceRow = PriceRow.builder()
                .material(ordinaryMaterial)
                .standardPrice(170.00)
                .discountLevel(6)
                .needsApproval(false)
                .build();

        Material dangerousMaterial = createDangerousMaterial();
        PriceRow priceRow = PriceRow.builder()
                .material(dangerousMaterial)
                .standardPrice(16566.00)
                .discountLevel(3)
                .needsApproval(false)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOfficeName("Skien")
                .salesOffice("104")
                .materialList(List.of(priceRow, ordinaryWastePriceRow))
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(salesOffice))
                .needsApproval(true)
                .build();

        priceOffer = service.save(priceOffer);

        PriceOfferTerms priceOfferTerms = PriceOfferTerms.builder()
                .customerNumber("169239")
                .customerName("Monica")
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(currentDateTime.toDate())
                .salesOrg("100")
                .salesOffice("100")
                .build();


        Boolean actual = service.activatePriceOffer(salesEmployee.getId(), priceOffer.getId(), priceOfferTerms);

        assertThat(actual, is(true));
    }

    private Material createOrdinaryMaterial() {
        String materialNumber = "159904";

        MaterialPrice wastePrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(170.00)
                .build();

        return Material.builder()
                .salesOffice("104")
                .salesOrg("100")
                .designation("Degaussing harddisker")
                .materialNumber(materialNumber)
                .scaleQuantum(0.0)
                .pricingUnit(1)
                .quantumUnit("ST")
                .materialGroup("1599")
                .materialGroupDesignation("Blandet EE-avfall")
                .materialType("ZWAF")
                .materialTypeDescription("Avfallsmateriale")
                .materialStandardPrice(wastePrice)
                .build();
    }

    private static Material createDangerousMaterial() {
        String materialNumber = "70120015";
        MaterialPrice wastePrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(16566.00)
                .build();
        return Material.builder()
                .materialNumber(materialNumber)
                .salesOrg("100")
                .salesOffice("100")
                .quantumUnit("KG")
                .designation("Ikke refunderbar spillolje,Småemb")
                .materialGroup("7012")
                .materialTypeDescription("Spillolje, ikke ref.")
                .materialType("ZAFA")
                .materialTypeDescription("Farlig Avfallsmateriale")
                .categoryId("00310")
                .categoryDescription("Farlig avfall")
                .subCategoryId("0031000100")
                .subCategoryDescription("FA Diverse")
                .pricingUnit(1000)
                .materialStandardPrice(wastePrice)
                .build();
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    void createMaterial() {
        String materialNumber = "119901";

        MaterialPrice wastePrice = MaterialPrice.builder()
        .materialNumber(materialNumber)
        .standardPrice(2456.00)
        .build();
        
        Material waste = Material.builder()
        .designation("Restavfall")
        .materialNumber(materialNumber)
        .pricingUnit(1000)
        .quantumUnit("KG")
        .materialGroup("9912")
        .materialGroupDesignation("Bl. næringsavfall")
        .materialType("ZWAF")
        .materialTypeDescription("Avfallsmateriale")
        .materialStandardPrice(wastePrice)
        .build();

        materialService.save(waste);
    }

    @Test
    public void shouldListAllPriceOfferForApprover() {
        User user = userService.findByEmail("alexander.brox@ngn.no");
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(user)
                .approver(user)
                .build();

        service.save(priceOffer);

        List<PriceOffer> actual = service.findAllByApproverIdAndPriceOfferStatus(user.getId(), null);

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldListAllPriceOfferForApproverWithStatusPending() {
        User user = userService.findByEmail("alexander.brox@ngn.no");
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(user)
                .approver(user)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        service.save(priceOffer);

        List<PriceOffer> actual = service.findAllByApproverIdAndPriceOfferStatus(user.getId(), PriceOfferStatus.PENDING.getStatus());

        assertThat(actual, hasSize(greaterThan(0)));
    }

    private void persistSalesRoles() {
        SalesRole knSalesRole = salesRoleService.findSalesRoleByRoleName("KN");

        if(knSalesRole == null) {
            knSalesRole = SalesRole.builder()
                    .roleName("KN")
                    .description("KAM nasjonalt")
                    .defaultPowerOfAttorneyOa(5)
                    .defaultPowerOfAttorneyFa(5)
                    .build();

            knSalesRole = salesRoleService.save(knSalesRole);
            System.out.println(knSalesRole);
        }

        SalesRole saSalesRole = salesRoleService.findSalesRoleByRoleName("SA");
        if(saSalesRole == null) {
            saSalesRole = SalesRole.builder()
                    .roleName("SA")
                    .description("Salgskonsulent (rolle a)")
                    .defaultPowerOfAttorneyOa(2)
                    .defaultPowerOfAttorneyFa(2)
                    .build();

            saSalesRole = salesRoleService.save(saSalesRole);

            System.out.println(saSalesRole);
        }
    }
}