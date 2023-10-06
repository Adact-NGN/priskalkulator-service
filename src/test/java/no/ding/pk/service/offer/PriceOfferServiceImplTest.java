package no.ding.pk.service.offer;

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
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.enums.TermsTypes;
import org.apache.commons.io.IOUtils;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SqlConfig(commentPrefix = "#")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(value = {"/discount_db_scripts/drop_schema.sql", "/discount_db_scripts/create_schema.sql"})
@Sql(value = {"/discount_db_scripts/discount_matrix.sql", "/discount_db_scripts/discount_levels.sql"})
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

    private ModelMapper modelMapper = new ModelMapper();
    private SapHttpClient sapHttpClient;
    private SapMaterialService sapMaterialService;

    StandardPriceService standardPriceService;


    @BeforeEach
    public void setup() {

        priceOfferRepository = getPriceOfferRepository();

        MaterialPriceService materialPriceService = new MaterialPriceServiceImpl(getMaterialPriceRepository());

        materialService = new MaterialServiceImpl(getMaterialRepository(), materialPriceService);

        sapMaterialService = mock(SapMaterialService.class);

        PriceRowService priceRowService = new PriceRowServiceImpl(getPriceRowRepository(),
                materialService,
                materialPriceService,
                getEmFactory(),
                sapMaterialService,
                modelMapper);

        discountService = mock(DiscountService.class);
        InMemory3DCache<String, String, MaterialStdPriceDTO> standardPriceInMemoryCache = new PingInMemory3DCache<>(5000);
        ModelMapper modelMapper = new ModelMapperV2Config().modelMapperV2(materialService, getSalesRoleRepository());
        sapHttpClient = mock(SapHttpClient.class);

        standardPriceService = mock(StandardPriceService.class);

        ZoneService zoneService = new ZoneServiceImpl(getZoneRepository(), priceRowService, discountService, standardPriceService);

        salesOfficeService = new SalesOfficeServiceImpl(getSalesOfficeRepository(), priceRowService, zoneService, standardPriceService);
        userService = new UserServiceImpl(getUserRepository(), getSalesRoleRepository());
        salesOfficePowerOfAttorneyService = new SalesOfficePowerOfAttorneyServiceImpl(getSalesOfficePowerOfAttorneyRepository());
        salesRoleService = new SalesRoleServiceImpl(getSalesRoleRepository());
        customerTermsService = new CustomerTermsServiceImpl(getCustomerTermsRepository());


        service = new PriceOfferServiceImpl(getPriceOfferRepository(), salesOfficeService, userService,
                salesOfficePowerOfAttorneyService, discountService, customerTermsService, modelMapper,
                List.of(100));

        prepareUsersAndSalesRoles();
        createMaterial();
        createDiscountMatrix();
    }


    @Test
    public void shouldPersistPriceOffer() throws IOException {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse();
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        ClassLoader classLoader = getClass().getClassLoader();
        mockMaterialServiceResponse(classLoader);

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

        MaterialPrice zoneMaterialPriceWithDeviceType = MaterialPrice.builder()
                .materialNumber("50301")
                .deviceType("B-0-S")
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
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse();
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

        HttpResponse<String> response = createResponse();
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

        HttpResponse<String> response = createResponse();
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        when(sapMaterialService.getAllMaterialsForSalesOrg(anyString(), anyInt(), anyInt())).thenReturn(List.of(MaterialDTO.builder()
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

        Material dangerousMaterial = materialService.findByMaterialNumber("70120015");
        if(dangerousMaterial == null) {
            dangerousMaterial = createDangerousMaterial();
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

    private static HttpResponse<String> createResponse() {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return 200;
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
                return "{\n" +
                        "    \"d\": {\n" +
                        "        \"results\": [\n" +
                        "            {\n" +
                        "                \"__metadata\": {\n" +
                        "                    \"id\": \"https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='159904')\",\n" +
                        "                    \"uri\": \"https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='159904')\",\n" +
                        "                    \"type\": \"ZPRICES_SRV.ZZStandPris\"\n" +
                        "                },\n" +
                        "                \"ValidFrom\": \"/Date(1695859200000)/\",\n" +
                        "                \"SalesOrganization\": \"100\",\n" +
                        "                \"SalesOffice\": \"104\",\n" +
                        "                \"Material\": \"159904\",\n" +
                        "                \"MaterialDescription\": \"Degaussing harddisker\",\n" +
                        "                \"DeviceCategory\": \"\",\n" +
                        "                \"SalesZone\": \"\",\n" +
                        "                \"ScaleQuantity\": \"0\",\n" +
                        "                \"StandardPrice\": \"170.00\",\n" +
                        "                \"Valuta\": \"\",\n" +
                        "                \"PricingUnit\": \"1\",\n" +
                        "                \"QuantumUnit\": \"ST\",\n" +
                        "                \"MaterialExpired\": \"\",\n" +
                        "                \"ValidTo\": \"/Date(253402214400000)/\",\n" +
                        "                \"MaterialGroup\": \"1599\",\n" +
                        "                \"MaterialGroupDescription\": \"Blandet EE-avfall\",\n" +
                        "                \"MaterialType\": \"ZWAF\",\n" +
                        "                \"MaterialTypeDescription\": \"Avfallsmateriale\"\n" +
                        "            },\n" +
                        "           {\n" +
                        "                \"__metadata\": {\n" +
                        "                    \"id\": \"https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='100',Material='70120015')\",\n" +
                        "                    \"uri\": \"https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-28T00%3A00%3A00',SalesOrganization='100',SalesOffice='100',Material='70120015')\",\n" +
                        "                    \"type\": \"ZPRICES_SRV.ZZStandPris\"\n" +
                        "                },\n" +
                        "                \"ValidFrom\": \"/Date(1695859200000)/\",\n" +
                        "                \"SalesOrganization\": \"100\",\n" +
                        "                \"SalesOffice\": \"104\",\n" +
                        "                \"Material\": \"70120015\",\n" +
                        "                \"MaterialDescription\": \"Ikke refunderbar spillolje,Småemb\",\n" +
                        "                \"DeviceCategory\": \"\",\n" +
                        "                \"SalesZone\": \"\",\n" +
                        "                \"ScaleQuantity\": \"0.000\",\n" +
                        "                \"StandardPrice\": \"16566.00\",\n" +
                        "                \"Valuta\": \"\",\n" +
                        "                \"PricingUnit\": \"1000\",\n" +
                        "                \"QuantumUnit\": \"KG\",\n" +
                        "                \"MaterialExpired\": \"\",\n" +
                        "                \"ValidTo\": \"/Date(253402214400000)/\",\n" +
                        "                \"MaterialGroup\": \"7012\",\n" +
                        "                \"MaterialGroupDescription\": \"Spillolje, ikke ref.\",\n" +
                        "                \"MaterialType\": \"ZAFA\",\n" +
                        "                \"MaterialTypeDescription\": \"Farlig Avfallsmateriale\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"__metadata\": {\n" +
                        "                    \"id\": \"https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-29T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='50301')\",\n" +
                        "                    \"uri\": \"https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet(ValidFrom=datetime'2023-09-29T00%3A00%3A00',SalesOrganization='100',SalesOffice='104',Material='50301')\",\n" +
                        "                    \"type\": \"ZPRICES_SRV.ZZStandPris\"\n" +
                        "                },\n" +
                        "                \"ValidFrom\": \"/Date(1695945600000)/\",\n" +
                        "                \"SalesOrganization\": \"100\",\n" +
                        "                \"SalesOffice\": \"104\",\n" +
                        "                \"Material\": \"50301\",\n" +
                        "                \"MaterialDescription\": \"Flatvogn - Utsett\",\n" +
                        "                \"DeviceCategory\": \"B-0-S\",\n" +
                        "                \"SalesZone\": \"\",\n" +
                        "                \"ScaleQuantity\": \"0\",\n" +
                        "                \"StandardPrice\": \"13.00\",\n" +
                        "                \"Valuta\": \"\",\n" +
                        "                \"PricingUnit\": \"1\",\n" +
                        "                \"QuantumUnit\": \"ST\",\n" +
                        "                \"MaterialExpired\": \"\",\n" +
                        "                \"ValidTo\": \"/Date(253402214400000)/\",\n" +
                        "                \"MaterialGroup\": \"0503\",\n" +
                        "                \"MaterialGroupDescription\": \"Tj.  Flatvogn\",\n" +
                        "                \"MaterialType\": \"DIEN\",\n" +
                        "                \"MaterialTypeDescription\": \"Tjeneste\"\n" +
                        "            }"+
                        "        ]\n" +
                        "    }\n" +
                        "}";
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
    public void shouldEndExistingCustomerTermsAndAddNewWhenNewPriceOfferIsActivated() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();
        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse();
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        when(sapMaterialService.getAllMaterialsForSalesOrg(anyString(), anyInt(), anyInt())).thenReturn(
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

        MaterialPrice wastePrice = getMaterialPriceRepository().findByMaterialNumber(materialNumber);

        if(wastePrice == null) {
            wastePrice = MaterialPrice.builder()
                    .materialNumber(materialNumber)
                    .standardPrice(2456.00)
                    .build();

            wastePrice = getMaterialPriceRepository().save(wastePrice);
        }

        Material waste = materialService.findByMaterialNumber(materialNumber);

        if(waste == null) {
            waste = Material.builder()
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

        HttpResponse<String> response = createResponse();
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

        HttpResponse<String> response = createResponse();
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

        HttpResponse<String> response = createResponse();
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

        HttpResponse<String> response = createResponse();
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        User user = userService.findByEmail("alexander.brox@ngn.no");

        MaterialPrice materialPrice = getMaterialPriceRepository().findByMaterialNumber("119901");

        if(materialPrice == null) {
            materialPrice = MaterialPrice.builder()
                    .materialNumber("119901")
                    .standardPrice(1199.0)
                    .build();
        }

        Material material = Material.builder()
                .materialNumber("119901")
                .materialStandardPrice(materialPrice)
                .build();

        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .discountLevel(7)
                .needsApproval(true)
                .build();
        List<PriceRow> materials = new LinkedList<PriceRow>();
        materials.add(priceRow);
        SalesOffice salesOffice = SalesOffice.builder()
                .salesOffice("100")
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

        HttpResponse<String> response = createResponse();
        when(sapHttpClient.getResponse(request)).thenReturn(response);

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

    @Test
    public void shouldSetPriceOfferStatusBackToPendingWhenDiscountLevelIsChanged() {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("http://test.com");
        HttpRequest request = HttpRequest.newBuilder().uri(urlBuilder.build().toUri()).build();

        doReturn(request).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> response = createResponse();
        when(sapHttpClient.getResponse(request)).thenReturn(response);

        List<String> materialNumbers = List.of("159904", "70120015");

        User dangerousWasteHolder = userService.findByEmail("alexander.brox@ngn.no");
        User ordinaryWasteHolder = userService.findByEmail("Eirik.Flaa@ngn.no");
        User ordinaryWasteHolderLvl2 = userService.findByEmail("kjetil.torvund.minde@ngn.no");

        User salesConsultant = userService.findByEmail("birte.sundmo@ngn.no");

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

        String salesOrg = "100";
        String salesOffice = "104";
        Material normalWaste = Material.builder()
                .materialNumber("159904")
                .salesOffice(salesOffice)
                .salesOrg(salesOrg)
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder()
                                .standardPrice(170.0)
                                .pricingUnit(1)
                                .materialNumber("159904")
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
                .salesOffice(salesOffice)
                .salesOrg(salesOrg)
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder()
                                .standardPrice(16566.0)
                                .pricingUnit(1000)
                                .materialNumber("70120015")
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
                .salesOffice(salesOffice)
                .salesOrg(salesOrg)
                .deviceType("B-0-S")
                .pricingUnit(1)
                .materialStandardPrice(
                        MaterialPrice.builder()
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

        priceOffer.getSalesOfficeList().get(0).getMaterialList().get(0).setDiscountLevel(3);

        PriceOffer actual = service.save(priceOffer);

        assertThat(actual.getPriceOfferStatus(), is(PriceOfferStatus.PENDING.getStatus()));
    }

    private void prepareUsersAndSalesRoles() {
        SalesRole knSalesRole = salesRoleService.findSalesRoleByRoleName("KN");

        if(knSalesRole == null) {
            knSalesRole = SalesRole.builder()
                    .roleName("KN")
                    .description("KAM nasjonalt")
                    .defaultPowerOfAttorneyOa(5)
                    .defaultPowerOfAttorneyFa(5)
                    .build();

            knSalesRole = salesRoleService.save(knSalesRole);
        }

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

        alex = userService.save(alex, null);

        knSalesRole.addUser(alex);

        knSalesRole = salesRoleService.save(knSalesRole);

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

        userService.save(eirik, null);

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

        userService.save(kjetil, null);

        User salesEmployee = User.builder()
                .adId("ad-id-wegarijo-arha-rh-arha")
                .jobTitle("Komponist")
                .fullName("Wolfgang Amadeus Mozart")
                .email("Wolfgang@farris-bad.no")
                .associatedPlace("Larvik")
                .department("Hvitsnippene")
                .build();

        salesEmployee = userService.save(salesEmployee, null);

        User consultant = User.builder()
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

        doReturn(materialDTOS).when(sapMaterialService).getAllMaterialsForSalesOrg(anyString(), anyInt(), anyInt());
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