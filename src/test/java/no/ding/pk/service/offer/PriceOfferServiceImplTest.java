package no.ding.pk.service.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.*;
import no.ding.pk.domain.offer.*;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.DiscountService;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.SalesRoleService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.enums.TermsTypes;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
class PriceOfferServiceImplTest {
    private PriceOfferService service;

    private PriceOfferRepository priceOfferRepository;

    private SalesOfficeService salesOfficeService;

    private UserService userService;

    private SalesRoleService salesRoleService;

    private MaterialService materialService;

    private SalesOfficePowerOfAttorneyService salesOfficePowerOfAttorneyService;

    private CustomerTermsService customerTermsService;

    private DiscountService discountService;

    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    public void setup() {

        priceOfferRepository = mock(PriceOfferRepository.class);
        salesOfficeService = mock(SalesOfficeService.class);
        userService = mock(UserService.class);
        salesOfficePowerOfAttorneyService = mock(SalesOfficePowerOfAttorneyService.class);
        discountService = mock(DiscountService.class);
        salesRoleService = mock(SalesRoleService.class);
        customerTermsService = mock(CustomerTermsService.class);
        materialService = mock(MaterialService.class);

        service = new PriceOfferServiceImpl(priceOfferRepository, salesOfficeService, userService,
                salesOfficePowerOfAttorneyService, discountService, customerTermsService, modelMapper);

        prepearUsersAndSalesRoles();
        createMaterial();
        createDiscountMatrix();



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
                .discountLevelPct(0.02)
                .showPriceInOffer(true)
                .manualPrice(2400.0)
                .discountLevel(1)
                .discountLevelPrice(56.0)
                .amount(1)
                .priceIncMva(2448.0)
                .standardPrice(2456.0)
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
                .discountLevelPct(0.02)
                .showPriceInOffer(true)
                .manualPrice(900.0)
                .discountLevel(1)
                .discountLevelPrice(100.0)
                .amount(1)
                .priceIncMva(1125.0)
                .standardPrice(1131.0)
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
        assertThat(priceOffer.getApprover(), equalTo(salesEmployee));
        assertThat(priceOffer.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));

        priceOffer2 = service.save(priceOffer2);

        assertThat(priceOffer2, notNullValue());
        assertThat(priceOffer.getApprover(), equalTo(salesEmployee));
        assertThat(priceOffer2.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer2.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer2.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldSetFaApproverWhenOnlyDangerousWastNeedsApproval() {
        when(discountService.findAllDiscountForDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(anyString(), anyString(), List.of("70120015")))
                .thenReturn(List.of(Discount.builder()
                                .salesOrg("100")
                                .salesOffice("104")
                                .fa("FA")
                                .materialDesignation("Spillolje ikke ref. - væske - småk")
                                .materialNumber("70120015")
                                .discountLevels(List.of(
                                        DiscountLevel.builder().level(1).discount(0.0).build(),
                                        DiscountLevel.builder().level(2).discount(1015.0).build(),
                                        DiscountLevel.builder().level(3).discount(2030.0).build(),
                                        DiscountLevel.builder().level(4).discount(3045.0).build(),
                                        DiscountLevel.builder().level(5).discount(3045.0).build()
                                )).build()
                        )
                );
        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        User salesEmployee = userService.findByEmail("alexander.brox@ngn.no");

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
                .salesEmployee(salesEmployee)
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

        customerTermsService.save(oldCustomerTerms.getSalesOffice(), oldCustomerTerms.getCustomerNumber(), oldCustomerTerms.getCustomerName(), oldCustomerTerms);

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
                .customerName("Monica")
                .customerNumber("169239")
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

    private void createDiscountMatrix() {
        String materialNumber = "119901";

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder()
                        .level(0)
                        .discount(0.0)
                        .build(),
                DiscountLevel.builder()
                        .level(1)
                        .discount(223.0)
                        .build(),
                DiscountLevel.builder()
                        .level(2)
                        .discount(446.0)
                        .build(),
                DiscountLevel.builder()
                        .level(3)
                        .discount(669.0)
                        .build(),
                DiscountLevel.builder()
                        .level(4)
                        .discount(895.0)
                        .build()
        );
        Discount discount = Discount.builder()
                .materialNumber(materialNumber)
                .salesOrg("100")
                .salesOffice("127")
                .materialDesignation("Restavfall")
                .build();

        discountLevels.forEach(discount::addDiscountLevel);

        discountService.save(discount);
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

        when(materialService.findByMaterialNumber(materialNumber)).thenReturn(waste);
    }

    @Test
    public void shouldListAllPriceOfferForApprover() {
        User user = userService.findByEmail("alexander.brox@ngn.no");
        List<SalesOffice> salesOfficeList = List.of(SalesOffice
                .builder()
                .salesOffice("100")
                .materialList(List.of())
                .build());
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesOfficeList(salesOfficeList)
                .salesEmployee(user)
                .approver(user)
                .build();

        List<PriceOffer> actual = service.findAllByApproverIdAndPriceOfferStatus(user.getId(), null);

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldListAllPriceOfferForApproverWithStatusPending() {
        User user = userService.findByEmail("alexander.brox@ngn.no");
        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber("119901")
                .standardPrice(1199.0)
                .build();
        Material material = Material.builder()
                .materialNumber("119901")
                .materialStandardPrice(materialPrice)
                .build();

        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .discountLevel(7)
                .needsApproval(true)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOffice("100")
                .materialList(List.of(priceRow))
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(user)
                .salesOfficeList(List.of(salesOffice))
                .approver(user)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        service.save(priceOffer);

        List<PriceOffer> actual = service.findAllByApproverIdAndPriceOfferStatus(user.getId(), PriceOfferStatus.PENDING.getStatus());

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldPersistMaterialWithDeviceType() {
        String materialNumber = "50301";
        String deviceType = "B-0040-FO";

        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(175.0)
                .pricingUnit(1)
                .quantumUnit("ST")
                .validTo(new Date(253402214400000L))
                .build();

        Material material = Material.builder()
                .salesOrg("100")
                .salesOffice("100")
                .materialNumber(materialNumber)
                .deviceType(deviceType)
                .materialTypeDescription("Tjeneste")
                .materialType("DIEN")
                .materialStandardPrice(materialPrice)
                .materialGroup("0503")
                .pricingUnit(1)
                .build();

        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .discountLevel(2)
                .needsApproval(false)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOffice("100")
                .materialList(List.of(priceRow))
                .build();

        User user = userService.findByEmail("alexander.brox@ngn.no");

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(user)
                .salesOfficeList(List.of(salesOffice))
                .approver(user)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        PriceOffer saved = service.save(priceOffer);

        Optional<PriceOffer> actual = service.findById(saved.getId());

        assertThat(actual.isPresent(), is(true));

        PriceOffer actualPriceOffer = actual.get();

        Material actualMaterial = actualPriceOffer.getSalesOfficeList().get(0).getMaterialList().get(0).getMaterial();

        assertThat(actualMaterial.getDeviceType(), notNullValue());
        assertThat(actualMaterial.getDeviceType(), equalTo(deviceType));

    }

    private void prepearUsersAndSalesRoles() {
        SalesRole knSalesRole = SalesRole.builder()
                .roleName("KN")
                .description("KAM nasjonalt")
                .defaultPowerOfAttorneyOa(5)
                .defaultPowerOfAttorneyFa(5)
                .build();


        User alex = User.builder()
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
                .salesRole(knSalesRole)
                .build();

        knSalesRole.addUser(alex);



        SalesRole saSalesRole = SalesRole.builder()
                .roleName("SA")
                .description("Salgskonsulent (rolle a)")
                .defaultPowerOfAttorneyOa(2)
                .defaultPowerOfAttorneyFa(2)
                .build();

        User eirik  = User.builder()
                .name("Eirik")
                .sureName("Flaa")
                .orgNr("100")
                .associatedPlace("Larvik")
                .email("Eirik.Flaa@ngn.no")
                .jobTitle("Prosjektleder")
                .powerOfAttorneyFA(5)
                .powerOfAttorneyOA(5)
                .build();

        User kjetil = User.builder()
                .name("Kjetil")
                .sureName("Minde")
                .orgNr("100")
                .associatedPlace("Larvik")
                .email("kjetil.torvund.minde@ngn.no")
                .jobTitle("Fullstack utvikler")
                .powerOfAttorneyFA(5)
                .powerOfAttorneyOA(5)
                .build();

        User salesEmployee = User.builder()
                .adId("ad-id-wegarijo-arha-rh-arha")
                .jobTitle("Komponist")
                .fullName("Wolfgang Amadeus Mozart")
                .email("Wolfgang@farris-bad.no")
                .associatedPlace("Larvik")
                .department("Hvitsnippene")
                .salesRole(saSalesRole)
                .build();

        saSalesRole.addUser(salesEmployee);

        when(userService.findByEmail("alexander.brox@ngn.no")).thenReturn(alex);
        when(userService.findByEmail("Eirik.Flaa@ngn.no")).thenReturn(eirik);
        when(userService.findByEmail("kjetil.torvund.minde@ngn.no")).thenReturn(kjetil);
        when(userService.findByEmail("Wolfgang Amadeus Mozart")).thenReturn(salesEmployee);
        when(salesRoleService.findSalesRoleByRoleName("KN"))
                .thenReturn(knSalesRole);
        when(salesRoleService.findSalesRoleByRoleName("SA"))
                .thenReturn(saSalesRole);

    }
}