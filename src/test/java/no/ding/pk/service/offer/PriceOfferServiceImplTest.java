package no.ding.pk.service.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.*;
import no.ding.pk.domain.offer.*;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.*;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.sap.StandardPriceService;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.dto.web.client.UserDTO;
import no.ding.pk.web.dto.web.client.offer.ContactPersonDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.enums.TermsTypes;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.ding.pk.utils.JsonTestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SqlConfig(commentPrefix = "#")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(value = {"classpath:discount_db_scripts/drop_schema.sql", "classpath:discount_db_scripts/create_schema.sql"})
@Sql(value = {"classpath:discount_db_scripts/tiny_discount_matrix.sql", "classpath:discount_db_scripts/tiny_discount_levels.sql"})
class PriceOfferServiceImplTest extends AbstractIntegrationConfig {
    private PriceOfferService service;

    private SalesOfficeService salesOfficeService;

    private UserService userService;

    private SalesRoleService salesRoleService;

    private MaterialService materialService;

    private SalesOfficePowerOfAttorneyService salesOfficePowerOfAttorneyService;

    private CustomerTermsService customerTermsService;

    private DiscountService discountService;

    private PriceOfferRepository priceOfferRepository;

    private ModelMapper modelMapper;
    private SapHttpClient sapHttpClient;
    private SapMaterialService sapMaterialService;

    StandardPriceService standardPriceService;


    @BeforeEach
    public void setup() {

        priceOfferRepository = getPriceOfferRepository();

        InMemory3DCache<String, String, MaterialPrice> materialPriceCache = new PingInMemory3DCache<>(5000);
        MaterialPriceService materialPriceService = new MaterialPriceServiceImpl(getMaterialPriceRepository(), materialPriceCache);

        materialService = new MaterialServiceImpl(getMaterialRepository(), materialPriceService);

        sapMaterialService = mock(SapMaterialService.class);

        discountService = mock(DiscountService.class);

        modelMapper = new ModelMapperV2Config().modelMapperV2(materialService, getSalesRoleRepository());

        PriceRowService priceRowService = new PriceRowServiceImpl(
                discountService,
                getPriceRowRepository(),
                materialService,
                getEmFactory(),
                sapMaterialService,
                modelMapper);

        InMemory3DCache<String, String, MaterialStdPriceDTO> standardPriceInMemoryCache = new PingInMemory3DCache<>(5000);
        ModelMapper modelMapper = new ModelMapperV2Config().modelMapperV2(materialService, getSalesRoleRepository());
        sapHttpClient = mock(SapHttpClient.class);

        standardPriceService = mock(StandardPriceService.class);
        Map<String, MaterialPrice> materialPrices = getMaterialPrices();
        doReturn(materialPrices).when(standardPriceService).getStandardPriceForSalesOrgAndSalesOfficeMap(anyString(), anyString(), any());

        ZoneService zoneService = new ZoneServiceImpl(getZoneRepository(), priceRowService, standardPriceService);

        salesOfficeService = new SalesOfficeServiceImpl(getSalesOfficeRepository(), priceRowService, zoneService, standardPriceService);
        userService = new UserServiceImpl(getUserRepository(), getSalesRoleRepository());
        salesOfficePowerOfAttorneyService = new SalesOfficePowerOfAttorneyServiceImpl(getSalesOfficePowerOfAttorneyRepository());
        salesRoleService = new SalesRoleServiceImpl(getSalesRoleRepository());
        customerTermsService = new CustomerTermsServiceImpl(getCustomerTermsRepository());


        service = new PriceOfferServiceImpl(getPriceOfferRepository(), getContactPersonRepository(), salesOfficeService, userService,
                salesOfficePowerOfAttorneyService, customerTermsService, modelMapper,
                List.of(100));

        prepareUsersAndSalesRoles();
        createMaterial();
        createDiscountMatrix();
    }

    private Map<String, MaterialPrice> getMaterialPrices() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("standardPrices100104.json").getFile());

        assertThat(file.exists(), is(true));

        try {
            String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

            JSONObject result = new JSONObject(json);

            JSONArray jsonArray = result.getJSONObject("d").getJSONArray("results");

            ObjectMapper om = getObjectMapper();

            List<MaterialPrice> materialPrices = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                MaterialStdPriceDTO materialStdPriceDTO = om.readValue(object.toString(), MaterialStdPriceDTO.class);

                MaterialPrice materialPrice = modelMapper.map(materialStdPriceDTO, MaterialPrice.class);
                materialPrices.add(materialPrice);
            }

            return materialPrices.stream().collect(Collectors.toMap(MaterialPrice::getUniqueMaterialNumber, Function.identity()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void shouldPersistPriceOffer() throws IOException {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        ClassLoader classLoader = getClass().getClassLoader();
        mockMaterialServiceResponse(classLoader);

        PriceOfferTerms priceOfferTerms = PriceOfferTerms.builder()
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(new Date())
                .build();

        MaterialPrice residualWasteMaterialStdPrice = MaterialPrice.builder("100", "100", "119901", null, "01")
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

        MaterialPrice zoneMaterialStandardPrice = MaterialPrice.builder("100", "100", "50101", null, null)
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

        MaterialPrice zoneMaterialPriceWithDeviceType = MaterialPrice.builder("100", "100", "50301", "B-0-S", null)
                .standardPrice(16.0)
                .build();
        Material zoneDiveceTypeMaterial = Material.builder()
                .materialNumber("50301")
                .deviceType("B-0-S")
                .designation("Flatvogn - Utsett")
                .materialGroupDesignation("Tj.  Flatvogn")
                .pricingUnit(1)
                .quantumUnit("ST")
                .materialStandardPrice(zoneMaterialPriceWithDeviceType)
                .build();
        PriceRow zoneDeviceTypeRow = PriceRow.builder()
                .customerPrice(15.0)
                .discountLevelPct(0.07)
                .showPriceInOffer(true)
                .manualPrice(15.0)
                .discountLevel(1)
                .discountLevelPrice(1.0)
                .amount(1)
                .priceIncMva(18.75)
                .standardPrice(16.0)
                .material(zoneDiveceTypeMaterial)
                .build();

        List<PriceRow> zoneMaterialList = List.of(zonePriceRow, zoneDeviceTypeRow);
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

        ContactPerson contactPerson = ContactPerson.builder()
                .firstName("Test")
                .lastName("Testesen")
                .emailAddress("test.testesen@testing.com")
                .mobileNumber("98765432")
                .build();

        List<ContactPerson> contactPeople = new ArrayList<>();
        contactPeople.add(contactPerson);

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("5162")
                .customerName("Europris Telem Notodden")
                .contactPersonList(contactPeople)
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
        assertThat(priceOffer.getContactPersonList(), hasSize(1));
        assertThat(priceOffer.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));

        priceOffer2 = service.save(priceOffer2);

        assertThat(priceOffer2, notNullValue());
        assertThat(priceOffer.getApprover(), equalTo(salesEmployee));
        assertThat(priceOffer.getContactPersonList(), hasSize(1));
        assertThat(priceOffer2.getSalesOfficeList(), notNullValue());
        assertThat(priceOffer2.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer2.getSalesOfficeList().get(0).getMaterialList(), hasSize(greaterThan(0)));
    }

    @Test
    public void shouldUpdateContactPersonList() {
        User salesEmployee = userService.findByEmail("alexander.brox@ngn.no");

        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOffice("127")
                .salesOfficeName("Sarpsborg/Fredrikstad")
                .postalNumber("1601")
                .city("FREDRIKSTAD")
                //.materialList(materialList)
                //.zoneList(zoneList)
                .build();

        ContactPerson contactPerson = ContactPerson.builder()
                .firstName("Test")
                .lastName("Testesen")
                .emailAddress("test.testesen@testing.com")
                .mobileNumber("98765432")
                .build();

        List<ContactPerson> contactPeople = new ArrayList<>();
        contactPeople.add(contactPerson);

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("327342")
                .customerName("Follo Ren IKS")
                .needsApproval(false)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .contactPersonList(contactPeople)
                .salesEmployee(salesEmployee)
                //.salesOfficeList(List.of(salesOffice2))
                .build();

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getContactPersonList(), hasSize(1));
        assertThat(priceOffer.getContactPersonList().get(0).getId(), notNullValue());

        PriceOffer updatedPriceOffer = PriceOffer.priceOfferBuilder()
                .id(priceOffer.getId())
                .customerNumber("327342")
                .customerName("Follo Ren IKS")
                .needsApproval(false)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                //.contactPersonList(contactPeople)
                .salesEmployee(salesEmployee)
                //.salesOfficeList(List.of(salesOffice2))
                .build();

        updatedPriceOffer.setContactPersonList(Collections.singletonList(ContactPerson.builder()
                .firstName("Test")
                .lastName("Testesen")
                .emailAddress("test.testesen@testing.com")
                .mobileNumber("98765432")
                .build()));

        assertThat(updatedPriceOffer.getContactPersonList(), hasSize(1));

        updatedPriceOffer = service.save(updatedPriceOffer);

        assertThat(updatedPriceOffer.getContactPersonList(), hasSize(1));

        updatedPriceOffer.addContactPerson(ContactPerson.builder()
                .firstName("Test2")
                .lastName("Testesen")
                .emailAddress("test2.testesen@testing.com")
                .mobileNumber("23456789")
                .build()
        );

        updatedPriceOffer = service.save(updatedPriceOffer);

        assertThat(updatedPriceOffer.getContactPersonList(), hasSize(2));
    }

    @Test
    public void shouldSetFaApproverWhenOnlyDangerousWastNeedsApproval() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        User salesEmployee = userService.findByEmail("alexander.brox@ngn.no");

        PowerOfAttorney powerOfAttorney = salesOfficePowerOfAttorneyService.findBySalesOffice(104);

        if(powerOfAttorney == null) {
            powerOfAttorney = PowerOfAttorney.builder()
                    .salesOffice(104)
                    .salesOfficeName("Skien")
                    .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                    .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                    .dangerousWasteHolder(dangerousWasteHolder)
                    .build();
            salesOfficePowerOfAttorneyService.save(powerOfAttorney);
        }

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

        assertThat(actual.getSalesOfficeList().get(0).getMaterialList().get(0).isApproved(), is(false));
    }

    @Test
    public void shouldSetOrdinaryLvlOneApproverWhenDangerousWastAndOrdinaryMaterialNeedsApproval() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        PowerOfAttorney powerOfAttorney = salesOfficePowerOfAttorneyService.findBySalesOffice(104);

        if(powerOfAttorney == null) {
            powerOfAttorney = PowerOfAttorney.builder()
                    .salesOffice(104)
                    .salesOfficeName("Skien")
                    .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                    .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                    .dangerousWasteHolder(dangerousWasteHolder)
                    .build();
            salesOfficePowerOfAttorneyService.save(powerOfAttorney);
        }

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
        assertThat(actual.getApprover(), equalTo(dangerousWasteHolder));
    }

    @Test
    public void shouldSetOrdinaryLvlTwoWhenDiscountLevelIsAtItsHighest() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        when(sapMaterialService.getAllMaterialsForSalesOrgBy(anyString(), anyInt(), anyInt())).thenReturn(List.of(MaterialDTO.builder()
                        .material("159904")
                        .categoryDescription("Degaussing harddisker")
                        .salesOrganization("100")
                .build()));

        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        PowerOfAttorney powerOfAttorney = salesOfficePowerOfAttorneyService.findBySalesOffice(104);

        if(powerOfAttorney == null) {
            powerOfAttorney = PowerOfAttorney.builder()
                    .salesOffice(104)
                    .salesOfficeName("Skien")
                    .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                    .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                    .dangerousWasteHolder(dangerousWasteHolder)
                    .build();
            powerOfAttorney = salesOfficePowerOfAttorneyService.save(powerOfAttorney);
        } else if(powerOfAttorney.getOrdinaryWasteLvlTwoHolder() == null ||
                powerOfAttorney.getOrdinaryWasteLvlOneHolder() == null) {
            powerOfAttorney.setOrdinaryWasteLvlOneHolder(ordinaryWasteHolder);
            powerOfAttorney.setOrdinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2);
            powerOfAttorney.setDangerousWasteHolder(dangerousWasteHolder);

            powerOfAttorney = salesOfficePowerOfAttorneyService.save(powerOfAttorney);
        }

        Material ordinaryMaterial = createOrdinaryMaterial();
        PriceRow ordinaryWastePriceRow = PriceRow.builder()
                .material(ordinaryMaterial)
                .standardPrice(170.00)
                .discountLevel(6)
                .needsApproval(true)
                .build();

        Optional<Material> optionalDangerousMaterial = materialService.findByMaterialNumber("70120015");
        Material dangerousMaterial;
        if(optionalDangerousMaterial.isEmpty()) {
            dangerousMaterial = createDangerousMaterial();
        } else {
            dangerousMaterial = optionalDangerousMaterial.get();
        }

        PriceRow faPriceRow = PriceRow.builder()
                .material(dangerousMaterial)
                .standardPrice(16566.00)
                .discountLevel(3)
                .needsApproval(true)
                .build();
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOfficeName("Skien")
                .salesOffice("104")
                .materialList(List.of(
                        faPriceRow,
                        ordinaryWastePriceRow))
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .salesEmployee(dangerousWasteHolder)
                .salesOfficeList(List.of(salesOffice))
                .needsApproval(true)
                .build();

        PriceOffer actual = service.save(priceOffer);

        assertThat(actual.getApprover(), notNullValue());
        assertThat(actual.getApprover().getEmail(), equalTo(ordinaryWasteHolderLvl2.getEmail()));
    }

    @Test
    public void shouldCreateOfferNeedingApprovalThenApproveThenUpdateItAndSetItBackToPending() throws IOException {

        PriceOfferDTO offerDto = createCompleteOfferDto("price_offers/82.json");

        ContactPersonDTO contactPersonDTO = offerDto.getContactPerson();

        ContactPerson contactPerson = modelMapper.map(contactPersonDTO, ContactPerson.class);

        contactPerson.setId(null);

        UserDTO salesEmployeeDto = offerDto.getSalesEmployee();
        User salesEmployee = modelMapper.map(salesEmployeeDto, User.class);

        salesEmployee = getUserRepository().save(salesEmployee);

        UserDTO approverDto = offerDto.getApprover();
        User approver = modelMapper.map(approverDto, User.class);

        approver = getUserRepository().save(approver);

        Integer salesOffice = Integer.valueOf(offerDto.getSalesOfficeList().get(0).getSalesOffice());

        PowerOfAttorney powerOfAttorney = getSalesOfficePowerOfAttorneyRepository().findBySalesOffice(salesOffice);

        if(powerOfAttorney == null) {
            powerOfAttorney = PowerOfAttorney.builder()
                    .salesOffice(salesOffice)
                    .ordinaryWasteLvlTwoHolder(approver)
                    .build();

            powerOfAttorney = getSalesOfficePowerOfAttorneyRepository().save(powerOfAttorney);
        }

        if(powerOfAttorney.getOrdinaryWasteLvlTwoHolder() == null) {
            powerOfAttorney.setOrdinaryWasteLvlTwoHolder(approver);
            powerOfAttorney = getSalesOfficePowerOfAttorneyRepository().save(powerOfAttorney);
        }

        PriceOffer priceOffer = modelMapper.map(offerDto, PriceOffer.class);

        priceOffer.setApprover(approver);
        priceOffer.setSalesEmployee(salesEmployee);

        priceOffer.setContactPersonList(new ArrayList<>());

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));

        Boolean isPriceOfferApproved = service.approvePriceOffer(priceOffer.getId(), approver.getId(), PriceOfferStatus.APPROVED.getStatus(), null);

        assertThat(isPriceOfferApproved, is(true));

        priceOffer = service.findById(priceOffer.getId()).orElse(null);
        assertThat(priceOffer.getPriceOfferStatus(), is(PriceOfferStatus.APPROVED.getStatus()));

        assertThat(priceOffer, notNullValue());

        List<PriceRow> priceRows = priceOffer.getSalesOfficeList().get(0).getMaterialList().stream().filter(priceRow -> StringUtils.equals(priceRow.getMaterial().getMaterialNumber(), "171194")).toList();

        assertThat(priceRows, hasSize(1));

        priceRows.get(0).setNeedsApproval(true);
        priceRows.get(0).setApproved(false);
        priceRows.get(0).setManualPrice(1200.0);
        priceRows.get(0).setDiscountLevel(4);

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getPriceOfferStatus(), is(PriceOfferStatus.APPROVED.getStatus()));

        priceRows = priceOffer.getSalesOfficeList().get(0).getMaterialList().stream().filter(priceRow -> StringUtils.equals(priceRow.getMaterial().getMaterialNumber(), "171194")).toList();

        assertThat(priceRows, hasSize(1));

        priceRows.get(0).setNeedsApproval(true);
        priceRows.get(0).setApproved(false);
        priceRows.get(0).setManualPrice(100.0);
        priceRows.get(0).setDiscountLevel(6);

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));
    }

    private static HttpResponse<String> createResponse(final int responseStatusCode) {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return responseStatusCode;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return """
                        {
                            "d": {
                                "results": [
                                    {
                                        "__metadata": {
                                            "id": "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='159904')",
                                            "uri": "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='159904')",
                                            "type": "ZPRICES_SRV.ZZStandPris"
                                        },
                                        "ValidFrom": "/Date(1695859200000)/",
                                        "SalesOrganization": "100",
                                        "SalesOffice": "104",
                                        "Material": "159904",
                                        "MaterialDescription": "Degaussing harddisker",
                                        "DeviceCategory": "",
                                        "SalesZone": "",
                                        "ScaleQuantity": "0",
                                        "StandardPrice": "170.00",
                                        "Valuta": "",
                                        "PricingUnit": "1",
                                        "QuantumUnit": "ST",
                                        "MaterialExpired": "",
                                        "ValidTo": "/Date(253402214400000)/",
                                        "MaterialGroup": "1599",
                                        "MaterialGroupDescription": "Blandet EE-avfall",
                                        "MaterialType": "ZWAF",
                                        "MaterialTypeDescription": "Avfallsmateriale"
                                    },
                                   {
                                        "__metadata": {
                                            "id": "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='100',Material='70120015')",
                                            "uri": "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='100',Material='70120015')",
                                            "type": "ZPRICES_SRV.ZZStandPris"
                                        },
                                        "ValidFrom": "/Date(1695859200000)/",
                                        "SalesOrganization": "100",
                                        "SalesOffice": "104",
                                        "Material": "70120015",
                                        "MaterialDescription": "Ikke refunderbar spillolje,Småemb",
                                        "DeviceCategory": "",
                                        "SalesZone": "",
                                        "ScaleQuantity": "0.000",
                                        "StandardPrice": "16566.00",
                                        "Valuta": "",
                                        "PricingUnit": "1000",
                                        "QuantumUnit": "KG",
                                        "MaterialExpired": "",
                                        "ValidTo": "/Date(253402214400000)/",
                                        "MaterialGroup": "7012",
                                        "MaterialGroupDescription": "Spillolje, ikke ref.",
                                        "MaterialType": "ZAFA",
                                        "MaterialTypeDescription": "Farlig Avfallsmateriale"
                                    },
                                    {
                                        "__metadata": {
                                            "id": "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-29T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='50301')",
                                            "uri": "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-29T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='50301')",
                                            "type": "ZPRICES_SRV.ZZStandPris"
                                        },
                                        "ValidFrom": "/Date(1695945600000)/",
                                        "SalesOrganization": "100",
                                        "SalesOffice": "104",
                                        "Material": "50301",
                                        "MaterialDescription": "Flatvogn - Utsett",
                                        "DeviceCategory": "B-0-S",
                                        "SalesZone": "",
                                        "ScaleQuantity": "0",
                                        "StandardPrice": "13.00",
                                        "Valuta": "",
                                        "PricingUnit": "1",
                                        "QuantumUnit": "ST",
                                        "MaterialExpired": "",
                                        "ValidTo": "/Date(253402214400000)/",
                                        "MaterialGroup": "0503",
                                        "MaterialGroupDescription": "Tj.  Flatvogn",
                                        "MaterialType": "DIEN",
                                        "MaterialTypeDescription": "Tjeneste"
                                    }        ]
                            }
                        }""";
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }

    @Test
    public void shouldSetEquivalentDiscountLevelForMaterialsWithManualPriceSet() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        List<DiscountLevel> discountLevels = Arrays.asList(
                DiscountLevel.builder(0.0, 1).build(),
                DiscountLevel.builder(199.0, 2).build(),
                DiscountLevel.builder(397.0, 3).build(),
                DiscountLevel.builder(695.0, 4).build(),
                DiscountLevel.builder(1033.0, 5).build()
        );
        Discount discount = Discount.builder("100", "100", "119901", 2604.0, discountLevels).build();

        when(discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn("100", "100", Collections.singletonList("119901"))).thenReturn(Collections.singletonList(discount));

        List<Zone> zones = Collections.singletonList(Zone.builder()
                .zoneId("0000000001")
                .postalCode("1001")
                .postalName("Oslo")
                .isStandardZone(true)
                .build()
        );
        MaterialPrice standardPrice = MaterialPrice.builder("100", "100", "119901", null, "01")
                .standardPrice(2604.0)
                .quantumUnit("KG")
                .materialNumber("119901")
                .build();

        when(standardPriceService.getStandardPriceForSalesOrgAndSalesOfficeMap("100", "100", null)).thenReturn(Map.of("119901", standardPrice));

        Material material = Material.builder()
                .materialNumber("119901")
                .designation("Restavfall")
                .materialGroupDesignation("Bl. næringsavfall")
                .materialTypeDescription("Avfallsmateriale")
                .deviceType("")
                .materialStandardPrice(standardPrice)
                .build();
        List<PriceRow> materials = Collections.singletonList(
                PriceRow.builder()
                        .showPriceInOffer(true)
                        .manualPrice(1.0)
                        .standardPrice(2604.0)
                        .needsApproval(true)
                        .approved(false)
                        .categoryId("00300")
                        .categoryDescription("Avfall")
                        .subCategoryId("0030000100")
                        .subCategoryDescription("Blandet avfall")
                        .classId("")
                        .classDescription("")
                        .material(material)
                        .build());
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOffice("100")
                .salesOrg("100")
                .salesOfficeName("Stor-Oslo")
                .city("OSLO")
                .zoneList(zones)
                .materialList(materials)
                .build();
        List<SalesOffice> salesOffices = Collections.singletonList(salesOffice);

        User salesEmployee = userService.findByEmail("Eirik.Flaa@ngn.no");

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(salesEmployee)
                .customerNumber("125277")
                .customerName("TESTKUNDE")
                .streetAddress("Tårnfjellvegen")
                .postalNumber("3910")
                .city("Porsgrunn")
                .organizationNumber("000000000")
                .salesOfficeList(salesOffices)
                .build();

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getNeedsApproval(), is(true));

        assertThat(priceOffer.getMaterialsForApproval(), notNullValue());
        assertThat(priceOffer.getSalesOfficeList().get(0).getMaterialList().get(0).getDiscountLevel(), is(6));
    }

    @Test
    public void shouldEndExistingCustomerTermsAndAddNewWhenNewPriceOfferIsActivated() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        when(sapMaterialService.getAllMaterialsForSalesOrgBy(anyString(), anyInt(), anyInt())).thenReturn(
                List.of(
                        MaterialDTO.builder()
                                .material("159904")
                                .materialDescription("Degaussing harddisker")
                                .salesOrganization("100")
                                .build(),
                        MaterialDTO.builder()
                                .material("70120015")
                                .materialDescription("Ikke refunderbar spillolje,Småemb")
                                .salesOrganization("100")
                                .build()
                ));

        LocalDateTime currentDateTime = LocalDateTime.now();

        User salesEmployee = userService.findByEmail("alexander.brox@ngn.no");

        CustomerTerms oldCustomerTerms = CustomerTerms.builder()
                .customerNumber("169239")
                .customerName("Monica")
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(currentDateTime.minusYears(1).toDate())
                .salesEmployee(salesEmployee.getEmail())
                .salesOrg("100")
                .salesOffice("104")
                .build();

        customerTermsService.save(oldCustomerTerms.getSalesOffice(), oldCustomerTerms.getCustomerNumber(), oldCustomerTerms.getCustomerName(), oldCustomerTerms);

        Material ordinaryMaterial = createOrdinaryMaterial();
        PriceRow ordinaryWastePriceRow = PriceRow.builder()
                .material(ordinaryMaterial)
                .standardPrice(170.00)
                .discountLevel(5)
                .needsApproval(false)
                .build();

        Material dangerousMaterial = createDangerousMaterial();
        PriceRow priceRow = PriceRow.builder()
                .material(dangerousMaterial)
                .standardPrice(16566.00)
                .discountLevel(3)
                .needsApproval(false)
                .build();
        String salesOfficeNumber = "104";
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOrg("100")
                .salesOfficeName("Skien")
                .salesOffice(salesOfficeNumber)
                .materialList(List.of(priceRow, ordinaryWastePriceRow))
                .build();

        String customerNumber = "169239";
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerName("Monica")
                .customerNumber(customerNumber)
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(salesOffice))
                .needsApproval(true)
                .build();

        priceOffer = service.save(priceOffer);

        List<CustomerTerms> activeTermsList = customerTermsService.findAll(salesOfficeNumber, customerNumber);
        assertThat(activeTermsList, hasSize(1));

        PriceOfferTerms priceOfferTerms = PriceOfferTerms.builder()
                .customerNumber(customerNumber)
                .customerName("Monica")
                .contractTerm(TermsTypes.GeneralTerms.getValue())
                .agreementStartDate(currentDateTime.toDate())
                .salesOrg("100")
                .salesOffice("104")
                .build();


        Boolean actual = service.activatePriceOffer(salesEmployee.getId(), priceOffer.getId(), priceOfferTerms, null);

        assertThat(actual, is(true));

        List<CustomerTerms> actualActiveTermsList = customerTermsService.findAll(salesOfficeNumber, customerNumber).stream().filter(customerTerms -> customerTerms.getAgreementEndDate() == null).toList();

        assertThat(actualActiveTermsList, hasSize(1));
    }

    private void createDiscountMatrix() {
        String materialNumber = "119901";

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder(0.0, 0)
                        .build(),
                DiscountLevel.builder(223.0, 1)
                        .build(),
                DiscountLevel.builder(446.0, 2)
                        .build(),
                DiscountLevel.builder(669.0, 3)
                        .build(),
                DiscountLevel.builder(895.0, 4)
                        .build()
        );
        Discount discount = Discount.builder("100", "127", materialNumber, 2764.0, discountLevels)
                .materialDesignation("Restavfall")
                .build();

//        discountLevels.forEach(discount::addDiscountLevel);

        discountService.save(discount);
    }

    private Material createOrdinaryMaterial() {
        String materialNumber = "159904";

        String salesOffice = "104";
        String salesOrg = "100";
        MaterialPrice wastePrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
                .materialNumber(materialNumber)
                .standardPrice(170.00)
                .build();

        return Material.builder()
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
        String salesOrg = "100";
        String salesOffice = "100";
        String materialNumber = "70120015";
        MaterialPrice wastePrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, null)
                .materialNumber(materialNumber)
                .standardPrice(16566.00)
                .build();
        return Material.builder()
                .materialNumber(materialNumber)
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

        MaterialPrice wastePrice = getMaterialPriceRepository().findByMaterialNumber(materialNumber);

        if(wastePrice == null) {
            wastePrice = MaterialPrice.builder("100", "100", materialNumber, null, "01")
                    .materialNumber(materialNumber)
                    .standardPrice(2456.00)
                    .build();

            wastePrice = getMaterialPriceRepository().save(wastePrice);
        }

        Optional<Material> optionalWaste = materialService.findByMaterialNumber(materialNumber);

        if(optionalWaste.isEmpty()) {
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
    }

    @Test
    public void shouldListAllPriceOfferForApprover() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(HttpStatus.OK.value());
        when(sapHttpClient.getResponse(request)).thenReturn(response);

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

        service.save(priceOffer);

        List<PriceOffer> actual = service.findAllByApproverIdAndPriceOfferStatus(user.getId(), null);

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldListAllPriceOfferForSalesEmployee() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User user = userService.findByEmail("alexander.brox@ngn.no");
        List<SalesOffice> salesOfficeList = List.of(SalesOffice
                .builder()
                .salesOffice("100")
                .materialList(List.of())
                .build());
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesOfficeList(salesOfficeList)
                .salesEmployee(user)
                .salesEmployee(user)
                .approver(user)
                .build();

        service.save(priceOffer);

        List<PriceOffer> actual = service.findAllBySalesEmployeeId(user.getId(), null);

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldListAllPriceOfferForSalesEmployeeWithStatuses() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User user = userService.findByEmail("alexander.brox@ngn.no");
        List<SalesOffice> salesOfficeList = List.of(SalesOffice
                .builder()
                .salesOffice("100")
                .materialList(List.of())
                .build());
        PriceOffer approvedPriceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus(PriceOfferStatus.APPROVED.getStatus())
                .salesOfficeList(salesOfficeList)
                .salesEmployee(user)
                .salesEmployee(user)
                .approver(user)
                .build();

        service.save(approvedPriceOffer);

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus(PriceOfferStatus.APPROVED.getStatus())
                .salesOfficeList(salesOfficeList)
                .salesEmployee(user)
                .salesEmployee(user)
                .approver(user)
                .build();

        service.save(priceOffer);

        List<PriceOffer> actualApproved = service.findAllBySalesEmployeeId(user.getId(), List.of(PriceOfferStatus.APPROVED.getStatus()));

        assertThat(actualApproved, hasSize(2));

        PriceOffer pendingPriceOffer = PriceOffer.priceOfferBuilder()
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .salesOfficeList(salesOfficeList)
                .salesEmployee(user)
                .salesEmployee(user)
                .approver(user)
                .build();

        priceOfferRepository.save(pendingPriceOffer);

        List<PriceOffer> actualPending = service.findAllBySalesEmployeeId(user.getId(), List.of(PriceOfferStatus.PENDING.getStatus()));

        assertThat(actualPending, hasSize(1));

        List<PriceOffer> actualMixed = service.findAllBySalesEmployeeId(user.getId(), List.of(PriceOfferStatus.PENDING.getStatus(), PriceOfferStatus.APPROVED.getStatus()));

        assertThat(actualMixed, hasSize(3));
    }

    @Test
    public void shouldListAllPriceOfferForApproverWithStatusPending() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User user = userService.findByEmail("alexander.brox@ngn.no");

        String materialNumber = "119901";
        MaterialPrice materialPrice = getMaterialPriceRepository().findByMaterialNumber(materialNumber);

        String salesOfficeNumber = "100";

        if(materialPrice == null) {
            materialPrice = MaterialPrice.builder("100", salesOfficeNumber, materialNumber, null, "01")
                    .standardPrice(1199.0)
                    .build();
        }

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .materialStandardPrice(materialPrice)
                .build();

        material = materialService.save(material);
        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .discountLevel(7)
                .needsApproval(true)
                .build();
        List<PriceRow> materials = new LinkedList<>();
        materials.add(priceRow);

        SalesOffice salesOffice = SalesOffice.builder()
                .salesOffice(salesOfficeNumber)
                .materialList(materials)
                .build();
        List<SalesOffice> salesOffices = new LinkedList<>();
        salesOffices.add(salesOffice);
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(user)
                .salesOfficeList(salesOffices)
                .approver(user)
                .priceOfferStatus(PriceOfferStatus.PENDING.getStatus())
                .build();

        priceOfferRepository.save(priceOffer);

        List<PriceOffer> actual = service.findAllByApproverIdAndPriceOfferStatus(user.getId(), PriceOfferStatus.PENDING.getStatus());

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldPersistMaterialWithDeviceType() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        String materialNumber = "50301";
        String deviceType = "B-0040-FO";

        MaterialPrice materialPrice = MaterialPrice.builder("100", "100", materialNumber, deviceType, null)
                .standardPrice(175.0)
                .pricingUnit(1)
                .quantumUnit("ST")
                .validTo(new Date(253402214400000L))
                .build();

        Material material = Material.builder()
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

    @Test
    public void shouldSetPriceOfferStatusBackToPendingWhenDiscountLevelIsChanged() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        User salesConsultant = userService.findByEmail("birte.sundmo@ngn.no");

        PowerOfAttorney powerOfAttorney = salesOfficePowerOfAttorneyService.findBySalesOffice(104);

        createPowerOfAttorney(powerOfAttorney, ordinaryWasteHolder, ordinaryWasteHolderLvl2, dangerousWasteHolder);

        String salesOrg = "100";
        String salesOffice = "104";
        String materialNumber = "159904";
        Material normalWaste = Material.builder()
                .materialNumber(materialNumber)
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, null)
                                .standardPrice(170.0)
                                .pricingUnit(1)
                                .quantumUnit("ST")
                                .build())
                .designation("Degaussing harddisker")
                .build();

        PriceRow normalWasteRow = PriceRow.builder()
                .material(normalWaste)
                .standardPrice(170.0)
                .discountLevel(2)
                .needsApproval(false)
                .build();
        Material dangerousWaste = Material.builder()
                .materialNumber("70120015")
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder("100", "100", "70120015", null, null)
                                .standardPrice(16566.0)
                                .pricingUnit(1000)
                                .quantumUnit("KG")
                                .build())
                .designation("Ikke refunderbar spillolje,Småemb")
                .build();
        PriceRow dangerousWasteRow = PriceRow.builder()
                .material(dangerousWaste)
                .standardPrice(16566.0)
                .needsApproval(false)
                .build();

        Material deviceType = Material.builder()
                .materialNumber("50301")
                .deviceType("B-0-S")
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder("100", "100", "50301", "B-0-S", null)
                                .standardPrice(13.00)
                                .pricingUnit(1)
                                .quantumUnit("ST")
                                .build())
                .designation("Flatvogn - Utsett")
                .build();

        PriceRow deviceTypeRow = PriceRow.builder()
                .material(deviceType)
                .standardPrice(13.00)
                .needsApproval(false)
                .build();

        SalesOffice salesOffice1 = SalesOffice.builder()
                .salesOrg(salesOrg)
                .salesOffice(salesOffice)
                .salesOfficeName("Skien")
                .materialList(List.of(
                        normalWasteRow,
                        dangerousWasteRow,
                        deviceTypeRow
                ))
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(salesConsultant)
                .salesOfficeList(List.of(salesOffice1))
                .needsApproval(false)
                .build();

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getPriceOfferStatus(), is(PriceOfferStatus.APPROVED.getStatus()));

        priceOffer.getSalesOfficeList().get(0).getMaterialList().get(0).setDiscountLevel(5);
        priceOffer.getSalesOfficeList().get(0).getMaterialList().get(0).setNeedsApproval(true);
        priceOffer.getSalesOfficeList().get(0).getMaterialList().get(0).setApproved(false);

        PriceOffer actual = service.save(priceOffer);

        assertThat(actual.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));
    }

    @Test
    public void shouldSetPriceOfferStatusBackToPendingWhenDiscountLevelIsSetToHighestPossibleLevel() throws JsonProcessingException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<DiscountLevel> discountLevels = Arrays.asList(
                DiscountLevel.builder(0.0, 1).build(),
                DiscountLevel.builder(5.0, 2).build(),
                DiscountLevel.builder(10.0, 3).build(),
                DiscountLevel.builder(15.0, 4).build(),
                DiscountLevel.builder(20.0, 5).build()
        );
        when(discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn("100", "104", Collections.singletonList("159904"))).thenReturn(Collections.singletonList(Discount.builder("100", "104", "159904", 170.0, discountLevels).build()));

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse(200);
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        User salesConsultant = userService.findByEmail("birte.sundmo@ngn.no");

        PowerOfAttorney powerOfAttorney = salesOfficePowerOfAttorneyService.findBySalesOffice(104);

        createPowerOfAttorney(powerOfAttorney, ordinaryWasteHolder, ordinaryWasteHolderLvl2, dangerousWasteHolder);

        String salesOrg = "100";
        String salesOffice = "104";
        Material normalWaste = Material.builder()
                .materialNumber("159904")
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder(salesOrg, salesOffice, "159904", null, null)
                                .standardPrice(170.0)
                                .pricingUnit(1)
                                .quantumUnit("ST")
                                .build())
                .designation("Degaussing harddisker")
                .build();

        PriceRow normalWasteRow = PriceRow.builder()
                .material(normalWaste)
                .standardPrice(170.0)
                .discountLevel(2)
                .needsApproval(false)
                .build();
        Material dangerousWaste = Material.builder()
                .materialNumber("70120015")
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder(salesOrg, salesOffice, "70120015", null, null)
                                .standardPrice(16566.0)
                                .pricingUnit(1000)
                                .quantumUnit("KG")
                                .build())
                .designation("Ikke refunderbar spillolje,Småemb")
                .build();
        PriceRow dangerousWasteRow = PriceRow.builder()
                .material(dangerousWaste)
                .standardPrice(16566.0)
                .needsApproval(false)
                .build();

        Material deviceType = Material.builder()
                .materialNumber("50301")
                .deviceType("B-0-S")
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder(salesOrg, salesOffice, "50301", "B-0-S", null)
                                .standardPrice(13.00)
                                .pricingUnit(1)
                                .materialNumber("50301")
                                .deviceType("B-0-S")
                                .quantumUnit("ST")
                                .build())
                .designation("Flatvogn - Utsett")
                .build();

        PriceRow deviceTypeRow = PriceRow.builder()
                .material(deviceType)
                .standardPrice(13.00)
                .needsApproval(false)
                .build();

        SalesOffice salesOffice1 = SalesOffice.builder()
                .salesOrg(salesOrg)
                .salesOffice(salesOffice)
                .salesOfficeName("Skien")
                .materialList(List.of(
                        normalWasteRow,
                        dangerousWasteRow,
                        deviceTypeRow
                ))
                .build();

        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .salesEmployee(salesConsultant)
                .salesOfficeList(List.of(salesOffice1))
                .needsApproval(false)
                .build();

        priceOffer = service.save(priceOffer);

        assertThat(priceOffer.getPriceOfferStatus(), is(PriceOfferStatus.APPROVED.getStatus()));

        String json = objectToJson(priceOffer);

        PriceOffer updatedPriceOffer = jsonToObject(json, PriceOffer.class);

        PriceRow priceRow = updatedPriceOffer.getSalesOfficeList().get(0).getMaterialList().get(0);
        priceRow.setNeedsApproval(true);
        priceRow.setDiscountLevel(null);
        priceRow.setManualPrice(1.0);

        PriceOffer actual = service.save(updatedPriceOffer);

        assertThat(actual.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));
        assertThat(actual.getApprover(), is(ordinaryWasteHolderLvl2));
    }

    @Test
    public void shouldApprovePriceOffer() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("price_offers/218.json")).getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        assertThat("JSON is empty", json, not(emptyOrNullString()));

        JSONObject results = new JSONObject(json);

        ObjectMapper om = getObjectMapper();

        PriceOfferDTO priceOfferDTO = om.readValue(results.toString(), PriceOfferDTO.class);

        PriceOffer priceOffer = new ModelMapperV2Config().modelMapperV2(materialService, getSalesRoleRepository()).map(priceOfferDTO, PriceOffer.class);

        User approver = userService.findByEmail(priceOffer.getApprover().getEmail());

        if(approver == null) {
            approver = getUserRepository().save(approver);
        }
        priceOffer.setApprover(approver);

        User salesEmployee = userService.findByEmail(priceOffer.getSalesEmployee().getEmail());

        if(salesEmployee == null) {
            salesEmployee = getUserRepository().save(salesEmployee);
        }
        priceOffer.setSalesEmployee(salesEmployee);
        priceOffer.setNeedsApproval(true);

        priceOffer = priceOfferRepository.save(priceOffer);

        Boolean approveResult = service.approvePriceOffer(priceOffer.getId(), approver.getId(), PriceOfferStatus.APPROVED.getStatus(), null);

        assertThat(approveResult, is(true));
    }

    private void createPowerOfAttorney(PowerOfAttorney powerOfAttorney, User ordinaryWasteHolder, User ordinaryWasteHolderLvl2, User dangerousWasteHolder) {
        if(powerOfAttorney == null) {
            powerOfAttorney = PowerOfAttorney.builder()
                    .salesOffice(104)
                    .salesOfficeName("Skien")
                    .ordinaryWasteLvlOneHolder(ordinaryWasteHolder)
                    .ordinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2)
                    .dangerousWasteHolder(dangerousWasteHolder)
                    .build();
            salesOfficePowerOfAttorneyService.save(powerOfAttorney);
        } else if(powerOfAttorney.getOrdinaryWasteLvlTwoHolder() == null ||
                powerOfAttorney.getOrdinaryWasteLvlOneHolder() == null) {
            powerOfAttorney.setOrdinaryWasteLvlOneHolder(ordinaryWasteHolder);
            powerOfAttorney.setOrdinaryWasteLvlTwoHolder(ordinaryWasteHolderLvl2);
            powerOfAttorney.setDangerousWasteHolder(dangerousWasteHolder);

            salesOfficePowerOfAttorneyService.save(powerOfAttorney);
        }
    }

    private void prepareUsersAndSalesRoles() {
        SalesRole knSalesRole = salesRoleService.findSalesRoleByRoleName("KN");

        if(knSalesRole == null) {
            knSalesRole = SalesRole.builder("KN", 5, 5)
                    .description("KAM nasjonalt")
                    .build();

            knSalesRole = salesRoleService.save(knSalesRole);
        }

        User alex = User.builder("Alexander", "Brox", "Alexander Brox", "alexander.brox@ngn.no", "alexander.brox@ngn.no")
                .id(39L)
                .adId("e2f1963a-072a-4414-8a0b-6a3aa6988e0c")
                .orgNr("100")
                .resourceNr("63874")
                .associatedPlace("Oslo")
                .phoneNumber("95838638")
                .jobTitle("Markedskonsulent")
                .powerOfAttorneyOA(5)
                .powerOfAttorneyFA(3)
                .salesRole(knSalesRole)
                .build();

        alex = userService.save(alex, null);

        knSalesRole.addUser(alex);

        salesRoleService.save(knSalesRole);

        SalesRole saSalesRole = SalesRole.builder("SA", 2, 2)
                .description("Salgskonsulent (rolle a)")
                .build();

        User eirik  = User.builder("Eirik", "Flaa", "Eirik Flaa", "Eirik.Flaa@ngn.no", "Eirik.Flaa@ngn.no")
                .orgNr("100")
                .associatedPlace("Larvik")
                .jobTitle("Prosjektleder")
                .powerOfAttorneyFA(5)
                .powerOfAttorneyOA(5)
                .build();

        userService.save(eirik, null);

        User kjetil = User.builder("Kjetil", "Minde", "Kjetil Torvund Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no")
                .orgNr("100")
                .associatedPlace("Larvik")
                .jobTitle("Fullstack utvikler")
                .powerOfAttorneyFA(5)
                .powerOfAttorneyOA(5)
                .build();

        userService.save(kjetil, null);

        User salesEmployee = User.builder("Wolfgang Amadeus", "Mozart", "Wolfgang Amadeus Mozart", "Wolfgang@farris-bad.no", "Wolfgang@farris-bad.no")
                .adId("ad-id-wegarijo-arha-rh-arha")
                .jobTitle("Komponist")
                .associatedPlace("Larvik")
                .department("Hvitsnippene")
                .build();

        salesEmployee = userService.save(salesEmployee, null);

        User consultant = User.builder("Birte", "Sundmo", "Birte Sundmo", "birte.sundmo@ngn.no", "birte.sundmo@ngn.no")
                .adId("e2f1963a-072a-4414-8a0b-6a3aa6988e0c")
                .name("Birte")
                .sureName("Sundmo")
                .fullName("Birte Sundmo")
                .orgNr("100")
                .orgName("NG")
                .associatedPlace("Oslo")
                .email("birte.sundmo@ngn.no")
                .powerOfAttorneyFA(2)
                .powerOfAttorneyOA(2)
                .build();

        consultant = userService.save(consultant,null);

        saSalesRole.addUser(salesEmployee);
        saSalesRole.addUser(consultant);

        salesRoleService.save(saSalesRole);
    }

    private void mockMaterialServiceResponse(ClassLoader classLoader) throws IOException {
        File file = new File(classLoader.getResource("materials100.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        JSONObject jsonObjectResult = new JSONObject(json);

        JSONArray result = jsonObjectResult.getJSONArray("value");

        ObjectMapper om = new ObjectMapper();

        List<MaterialDTO> materialDTOS = new ArrayList<>();

        for(int i = 0; i < result.length(); i++) {
            JSONObject jsonObject = result.getJSONObject(i);

            MaterialDTO materialDTO = om.readValue(jsonObject.toString(), MaterialDTO.class);

            materialDTOS.add(materialDTO);
        }

        doReturn(materialDTOS).when(sapMaterialService).getAllMaterialsForSalesOrgBy(anyString(), anyInt(), anyInt());
    }

    private void mockCallForStandardPrice(ClassLoader classLoader) throws IOException {
        File file = new File(classLoader.getResource("standardPrices100104.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        doReturn(HttpRequest.newBuilder().uri(URI.create("https://test")).build()).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> stdPriceResponse = new HttpResponse<>() {
            @Override
            public int statusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return json;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };

        doReturn(stdPriceResponse).when(sapHttpClient).getResponse(any());
    }
}