package no.ding.pk.service.sap;

import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.service.*;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.offer.*;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.pricing.PricingEntityCombinationMap;
import no.ding.pk.web.dto.sap.pricing.SapCreatePricingEntitiesResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@Disabled("Integration test needing env.local file with SAP credentials.")
@DataJpaTest
@TestPropertySource("classpath:h2-db.properties")
class SapPricingServiceImplTest extends AbstractIntegrationConfig {

    private SapPricingService sapPricingService;

    private SalesOfficeService salesOfficeService;
    private UserService userService;
    private SalesOfficePowerOfAttorneyService poaService;
    private CustomerTermsService customerTermsService;
    private String sapPricingConditionRecordUrl;
    private PriceRowService priceRowService;
    private ZoneService zoneService;
    private StandardPriceService standardPriceService;
    private DiscountService discountService;
    private MaterialService materialService;
    private MaterialPriceService materialPriceService;
    private SapMaterialService sapMaterialService;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;
    private SalesOrgService salesOrgService;

    @BeforeEach
    public void setup() throws IOException {
        SapHttpClient sapHttpClient = new SapHttpClient("", "");

        String salesOrgServiceUrl = "";
        salesOrgService = new SalesOrgServiceImpl(salesOrgServiceUrl, getObjectMapper(), sapHttpClient);
        String standardPriceSapUrl = "";
        standardPriceService = new StandardPriceServiceImpl(standardPriceSapUrl, getObjectMapper(), new PingInMemory3DCache<>(5000), sapMaterialService,
                sapHttpClient, modelMapper, salesOrgService);
        String materialServiceUrl = "";
        sapMaterialService = new SapMaterialServiceImpl(materialServiceUrl, sapHttpClient, new LocalJSONUtils(getObjectMapper()), new PingInMemory3DCache<>(5000));
        materialPriceService = new MaterialPriceServiceImpl(getMaterialPriceRepository(), new PingInMemory3DCache<>(5000));
        materialService = new MaterialServiceImpl(getMaterialRepository(), materialPriceService);
        discountService = new DiscountServiceImpl(getDiscountRepository(), getDiscountLevelRepository());
        priceRowService = new PriceRowServiceImpl(discountService, getPriceRowRepository(), materialService, materialPriceService, getEmFactory(), sapMaterialService, modelMapper);
        zoneService = new ZoneServiceImpl(getZoneRepository(), priceRowService, standardPriceService);
        userService = new UserServiceImpl(getUserRepository(), getSalesRoleRepository());
        poaService = new SalesOfficePowerOfAttorneyServiceImpl(getSalesOfficePowerOfAttorneyRepository());
        customerTermsService = new CustomerTermsServiceImpl(getCustomerTermsRepository());
        sapPricingConditionRecordUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/API_SLSPRICINGCONDITIONRECORD_SRV/A_SlsPrcgConditionRecord";
        priceRowService = new PriceRowServiceImpl(discountService, getPriceRowRepository(), materialService,
                materialPriceService, getEmFactory(), sapMaterialService, new ModelMapper());
        salesOfficeService = new SalesOfficeServiceImpl(getSalesOfficeRepository(), priceRowService, zoneService, standardPriceService);
        PriceOfferService priceOfferService = new PriceOfferServiceImpl(getPriceOfferRepository(), getContactPersonRepository(), salesOfficeService,
                userService, poaService, customerTermsService, new ModelMapper(), List.of(100));

        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("env.local").getFile());

        String sapUsername = null;
        String sapPassword = null;

        for (String readLine : IOUtils.readLines(new FileInputStream(file), StandardCharsets.UTF_8)) {
            String[] split = readLine.split("=");

            switch (split[0]) {
                case "SAP_PASSWORD" -> sapPassword = split[1];
                case "SAP_USERNAME" -> sapUsername = split[1];
            }
        }

        assertThat("Username could not be found in env file.", sapUsername, notNullValue());
        assertThat("Password could not be found in env file.", sapPassword, notNullValue());

        sapPricingService = new SapPricingServiceImpl(priceOfferService, getObjectMapper(), sapPricingConditionRecordUrl,
                new SapHttpClient(sapUsername, sapPassword));
    }

    @Test
    public void shouldUpdateZr05MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZR05";
        String keyCombination = "704";
        String materialNumber = "111101";
        String valueUnit = "NOK";
        double rateValue = -1749.00;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Matavfall, uembalert")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(2878.0)
                                .material(material)
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldBatchUpdateZr05MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZR05";
        String keyCombination = "704";
        String materialNumber = "111101";
        String valueUnit = "NOK";
        double rateValue = -1749.00;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Matavfall, uembalert")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(
                                PriceRow.builder()
                                        .standardPrice(2878.0)
                                        .material(material)
                                        .build(),
                                PriceRow.builder()
                                        .standardPrice(3878.0)
                                        .material(Material.builder().materialNumber("111102").build())
                                        .build()
                        ))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap.builder(
                        salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);

        PricingEntityCombinationMap otherEntityCombinationMap = PricingEntityCombinationMap.builder(
                        salesOrg, salesOffice, "111102", conditionCode, "766",
                        -10.0, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(otherEntityCombinationMap);

        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.batchUpdateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZptrMaterialPrice() {

        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        double standardPrice = 1579.0;
        Material material = Material.builder()
                .materialNumber("50106")
                .designation("Lift - Haste/tidsbestemt oppdrag")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                                .salesOrg("100")
                                .salesOffice("107")
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                        .salesZone("01")
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();
        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder("100", "107", "50106", "ZPTR", "615",
                        -10.00,"NOK")
                .zone(1)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZprk704MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZPRK";
        String keyCombination = "704";
        String materialNumber = "119901";
        String valueUnit = "NOK";
        double rateValue = 1200.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZprk795MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZPRK";
        String keyCombination = "795";
        String materialNumber = "50405";
        String valueUnit = "NOK";
        double rateValue = 200.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                        .salesZone("01")
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .zone(1)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZprk798MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZPRK";
        String keyCombination = "798";
        String materialNumber = "50405";
        String valueUnit = "NOK";
        double rateValue = 200.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                .salesZone("01")
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .zone(1)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Disabled("ZH03 does not have access to condition table 615")
    @Test
    public void shouldUpdateZh03615MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZH03";
        String keyCombination = "615";
        String materialNumber = "50305";
        String valueUnit = "NOK";
        double rateValue = -10.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                        .salesZone("01")
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .zone(1)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZh00767MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZH00";
        String keyCombination = "767";
        String materialNumber = "119901";
        String valueUnit = "%";
        double rateValue = -10.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), "7190", priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZh02_767MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZH02";
        String keyCombination = "767";
        String materialNumber = "111101";
        String valueUnit = "NOK";
        double rateValue = -10.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), "7190", priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZh03_767MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZH03";
        String keyCombination = "767";
        String materialNumber = "111101";
        String valueUnit = "NOK";
        double rateValue = -10.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), "7190", priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }

    @Test
    public void shouldUpdateZr02_704MaterialPrice() {
        User salesEmployee = User.builder("Kjetil", "Minde", "Kjetil Minde",
                "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        String salesOrg = "100";
        String salesOffice = "100";
        String conditionCode = "ZR02";
        String keyCombination = "704";
        String materialNumber = "C-08CL";
        String valueUnit = "%";
        double rateValue = -50.00;
        double standardPrice = 2604.0;

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .build();
        PriceOffer priceOffer = PriceOffer.priceOfferBuilder()
                .customerNumber("115938")
                .customerName("Bjørløw Campingdrift AS")
                .salesEmployee(salesEmployee)
                .salesOfficeList(List.of(SalesOffice.builder()
                        .salesOrg(salesOrg)
                        .salesOffice(salesOffice)
                        .materialList(List.of(PriceRow.builder()
                                .standardPrice(standardPrice)
                                .material(material)
                                .build()))
                        .build()))
                .build();

        priceOffer = getPriceOfferRepository().save(priceOffer);

        List<PricingEntityCombinationMap> pricingEntityCombinationMaps = new ArrayList<>();

        PricingEntityCombinationMap entityCombinationMap = PricingEntityCombinationMap
                .builder(salesOrg, salesOffice, materialNumber, conditionCode, keyCombination,
                        rateValue, valueUnit)
                .build();

        pricingEntityCombinationMaps.add(entityCombinationMap);
        List<SapCreatePricingEntitiesResponse> actual = sapPricingService.updateMaterialPriceEntities(priceOffer.getId(),
                priceOffer.getCustomerNumber(), null, priceOffer.getCustomerName(),
                pricingEntityCombinationMaps);

        assertThat(actual, hasSize(1));
        assertThat(actual.get(0).isUpdated(), is(true));
    }
}