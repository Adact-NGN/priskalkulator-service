package no.ding.pk.service.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.DroolsConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.User;
import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.KeyCombination;
import no.ding.pk.domain.offer.*;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import no.ding.pk.repository.bo.KeyCombinationRepository;
import no.ding.pk.service.CustomerService;
import no.ding.pk.service.UserAzureAdService;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.utils.JsonTestUtils;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.mappers.MapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

//@Disabled("")
//@SpringBootTest()
//@TestPropertySource("/h2-db.properties")
@Sql(value = {
        "/conditional_code_key_combination_scripts/drop_schemas.sql",
        "/conditional_code_key_combination_scripts/create_condition_code.sql",
        "/conditional_code_key_combination_scripts/create_key_combination.sql"
})
@Sql(value = {
        "/conditional_code_key_combination_scripts/insert_condition_code_with_key_combination.sql"
})
@Import({DroolsConfig.class, ModelMapperV2Config.class})
public class BoReportConditionCodeServiceImplTest extends AbstractIntegrationConfig {

    private BoReportConditionCodeService service;

    @Autowired
    private ConditionCodeRepository conditionCodeRepository;

    @Autowired
    private KeyCombinationRepository keyCombinationRepository;

    @Autowired
    private KieContainer kieContainer;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private ConfidentialClientApplication confidentialClientApplication;

    @MockBean
    private MapperService mapperService;

    @MockBean
    private LocalJSONUtils localJSONUtils;

    @MockBean
    private UserAzureAdService userAzureAdServiceImpl;

    @MockBean
    private SapHttpClient sapHttpClient;

    @MockBean
    @Qualifier("materialInMemoryCache")
    private InMemory3DCache<String, String, MaterialDTO> inMemory3DCache;

    @MockBean
    @Qualifier("standardPriceInMemoryCache")
    private InMemory3DCache<String, String, MaterialStdPriceDTO> standardPriceInMemoryCache;

    @BeforeEach
    public void setup() {
        service = new BoReportConditionCodeServiceImpl(conditionCodeRepository,
                keyCombinationRepository,
                kieContainer);
    }

    @Test
    public void shouldGetConditionCodeList() {
        List<ConditionCode> allConditionCodes = service.getAllConditionCodes(null);

        assertThat(allConditionCodes, hasSize(10));
    }

    @Test
    public void shouldGetAllKeyCombinationsForConditionCode() {
        List<KeyCombination> keyCombinations = service.getKeyCombinationByConditionCode("ZR05");

        assertThat(keyCombinations, hasSize(6));
    }

    @Test
    public void shouldGetAllKeyCombinationsForConditionCodeZPRK() {
        List<KeyCombination> keyCombinations = service.getKeyCombinationByConditionCode("ZPRK");

        assertThat(keyCombinations, hasSize(8));
    }

    @Test
    public void shouldGetAllSuggestionsForPriceOffer() throws IOException {
        PriceOfferDTO completeOfferDto = JsonTestUtils.createCompleteOfferDto("priceOfferToGetSuggestions.json");

        PriceOffer priceOffer = createPriceOffer(); // modelMapper.map(completeOfferDto, PriceOffer.class);
        Map<String, Map<String, BoReportCondition>> stringMapMap = service.buildBoReportConditionMapForPriceOffer(priceOffer);

        assertThat(stringMapMap.keySet().size(), greaterThan(0));

        SalesOffice salesOffice = priceOffer.getSalesOfficeList().get(0);
        assertThat(stringMapMap.get(salesOffice.getSalesOffice()).size(), greaterThan(0));
    }

    private PriceOffer createPriceOffer() {
        PriceOfferTerms customerTerms = PriceOfferTerms.builder()
                .id(137L)
                .additionForAdminFee(true)
                .agreementStartDate(new Date(1690840800000L))
                .agreementEndDate(new Date(1696024800000L))
                .contractTerm("Generelle vilkår")
                .invoiceInterval("Hver 14.dag")
                .metalPricing("Ordinær regulering")
                .metalSetDateForOffer(new Date(1693519200000L))
                .paymentCondition("15 dgr")
                .build();
        User user = User.builder()
                .id(121L)
                .adId("380b1804-d7c8-4983-abee-7ff99a2a6f59")
                .orgNr("100")
                .orgName("ng")
                .sureName("Flaa")
                .name("Eirik")
                .username("Eirik.Flaa@ngn.no")
                .usernameAlias("vh1482")
                .jobTitle("Leder Operativt pristeam")
                .fullName("Eirik Flaa")
                .phoneNumber("+4797069605")
                .email("Eirik.Flaa@ngn.no")
                .associatedPlace("Larvik")
                .powerOfAttorneyOA(2)
                .powerOfAttorneyFA(3)
                .department("Operativt Pristeam")
                .build();

        Zone zone = Zone.builder()
                .id(177L)
                .zoneId("0000000001")
                .postalCode("3252")
                .postalName("Larvik")
                .isStandardZone(true)
                .priceRows(List.of())
                .build();

        List<PriceRow> materialList = List.of(
                PriceRow.builder()
                        .id(2819L)
                        .standardPrice(919.0)
                        .discountedPrice(724.0)
                        .manualPrice(790.0)
                        .showPriceInOffer(true)
                        .categoryId("00200")
                        .categoryDescription("Transport")
                        .subCategoryId("0020000110")
                        .subCategoryDescription("LIFTBIL TRANSPORT")
                        .material(Material.builder()
                                .id(2819L)
                                .materialNumber("50107")
                                .designation("Lift - Flytting")
                                .materialGroupDesignation("Tjeneste")
                                .quantumUnit("ST")
                                .pricingUnit(1)
                                .build())
                        .build()
                ,
                PriceRow.builder()
                        .id(2819L)
                        .standardPrice(1362.0)
                        .discountedPrice(724.0)
                        .manualPrice(790.0)
                        .showPriceInOffer(true)
                        .categoryId("00200")
                        .categoryDescription("Transport")
                        .subCategoryId("0020000110")
                        .subCategoryDescription("LIFTBIL TRANSPORT")
                        .material(Material.builder()
                                .id(2819L)
                                .materialNumber("50108")
                                .designation("Lift - Ventetid")
                                .materialGroupDesignation("Tjeneste")
                                .quantumUnit("TI")
                                .pricingUnit(1)
                                .build())
                        .build()

        );
        SalesOffice salesOffice = SalesOffice.builder()
                .id(110L)
                .salesOffice("105")
                .salesOrg("100")
                .salesOfficeName("Larvik")
                .city("LARVIK")
                .zoneList(List.of(zone))
                .materialList(materialList)
                .build();
        return PriceOffer.priceOfferBuilder()
                .priceOfferStatus("ACTIVATED")
                .customerNumber("238575")
                .customerName("Christine")
                .needsApproval(true)
                .priceOfferTerms(customerTerms)
                .approver(user)
                .salesEmployee(user)
                .salesOfficeList(List.of(salesOffice))
                .build();
    }
}
