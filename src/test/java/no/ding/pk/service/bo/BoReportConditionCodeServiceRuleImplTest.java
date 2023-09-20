
package no.ding.pk.service.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.DroolsConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import no.ding.pk.repository.bo.KeyCombinationRepository;
import no.ding.pk.service.CustomerService;
import no.ding.pk.service.UserAzureAdService;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Import({DroolsConfig.class, ModelMapperV2Config.class})
class BoReportConditionCodeServiceRuleImplTest extends AbstractIntegrationConfig {

    private BoReportConditionCodeService boReportService;

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
        boReportService = new BoReportConditionCodeServiceImpl(conditionCodeRepository, keyCombinationRepository, kieContainer);
    }

    @Test
    public void whenCriteriaMatchingThenSuggestSalesOfficePerMaterialPerZone() {

        BoReportCondition condition = BoReportCondition.builder()
                .terms("Generelle Vilkår")
                .hasSalesOrg(true)
                .isPricedOnSalesOffice(true)
                .isCustomer(true)
                .isNode(false)
                .isZoneMaterial(true)
                .isWaste(false)
                .hasDevicePlacement(false)
                .isDeviceType(false)
                .hasSalesDocument(false)
                .isWasteDisposalMaterial(false)
                .isService(false)
                .isRental(false)
                .isProduct(false)
                .build();

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZPTR"));
        assertThat(suggestion.getKeyCombination(), equalTo("Salgskontor per material per sone"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A615"));
    }

    @Test
    public void whenCriteriaMatchingThenSuggestSalesOfficePerMaterialPerWastePerZone() {
        BoReportCondition condition = BoReportCondition.builder()
                .terms("Generelle Vilkår")
                .hasSalesOrg(true)
                .isPricedOnSalesOffice(true)
                .isCustomer(true)
                .isNode(false)
                .isZoneMaterial(true)
                .isWaste(true)
                .hasDevicePlacement(false)
                .isDeviceType(false)
                .hasSalesDocument(false)
                .isWasteDisposalMaterial(false)
                .isService(false)
                .isRental(false)
                .isProduct(false)
                .build();

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZPTR"));
        assertThat(suggestion.getKeyCombination(), equalTo("Salgskontor per material per avfall per sone"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A783"));
    }

    @Test
    public void whenCriteriaMatchingThenSuggestDiscountSalesOfficePerCustomerPerMaterialPerWaste01() {
        BoReportCondition condition = BoReportCondition.builder()
                .terms("Generelle Vilkår")
                .hasSalesOrg(true)
                .isPricedOnSalesOffice(true)
                .isCustomer(true)
                .isNode(false)
                .isZoneMaterial(false)
                .isWaste(true)
                .hasDevicePlacement(false)
                .isDeviceType(false)
                .hasSalesDocument(false)
                .isWasteDisposalMaterial(true)
                .isService(false)
                .isRental(false)
                .isProduct(false)
                .build();

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZR05"));
        assertThat(suggestion.getKeyCombination(), equalTo("Rabatt salgskontor per kunde per material per avfall"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("?"));
    }

    @Test
    public void whenCriteriaMatchingThenSuggestDiscountPerCustomerMaterialZone() {
        BoReportCondition condition = BoReportCondition.builder()
                .terms("NG prisvilkår")
                .hasSalesOrg(true)
                .isPricedOnSalesOffice(true)
                .isCustomer(true)
                .isNode(false)
                .isZoneMaterial(false)
                .isWaste(false)
                .hasDevicePlacement(false)
                .isDeviceType(false)
                .hasSalesDocument(false)
                .isWasteDisposalMaterial(false)
                .isService(false)
                .isRental(false)
                .isProduct(false)
                .build();

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZPRK"));
        assertThat(suggestion.getKeyCombination(), equalTo("Rabatt per kunde/material"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A704"));
    }

    @Disabled("Weak rules to be able to distinguish this result.")
    @Test
    public void whenCriteriaMatchingThenSuggestCustomerHierarchyPerMaterial() {
        BoReportCondition condition = BoReportCondition.builder()
                .terms("Kundens vilkår")
                .hasSalesOrg(true)
                .isPricedOnSalesOffice(false)
                .isCustomer(false)
                .isNode(true)
                .isZoneMaterial(false)
                .isWaste(false)
                .hasDevicePlacement(false)
                .isDeviceType(false)
                .hasSalesDocument(false)
                .isWasteDisposalMaterial(false)
                .isService(false)
                .isRental(false)
                .isProduct(false)
                .build();

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZH00"));
        assertThat(suggestion.getKeyCombination(), equalTo("Kundehiearki per material"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A767"));
    }
}