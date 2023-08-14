package no.ding.pk.service.bo;

import no.ding.pk.config.RulesConfig;
import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class BoReportTitleTypeServiceImplTest {

    @MockBean
    private ConditionCodeRepository ccRepo;

    private final KieSession kieSession = new RulesConfig().getKieSession();

    private final BoReportTitleTypeService boReportService = new BoReportTitleTypeServiceImpl(ccRepo, kieSession);

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
                .build();

        SuggestedConditionCodeKeyCombination suggestion = new SuggestedConditionCodeKeyCombination();

        boReportService.suggestConditionCodeAndKeyCombination(condition, suggestion);

        assertThat(suggestion.getConditionCode(), equalTo("ZPTR"));
        assertThat(suggestion.getKeyCombination(), equalTo("Salgskontor per material per sone"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A615"));
    }
}