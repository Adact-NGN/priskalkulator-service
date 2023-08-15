package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class BoReportTitleTypeServiceImplTest {

    @Autowired
    private BoReportTitleTypeService boReportService;

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

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZPTR"));
        assertThat(suggestion.getKeyCombination(), equalTo("Salgskontor per material per sone"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A615"));
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
                .isWaste(true)
                .hasDevicePlacement(false)
                .isDeviceType(false)
                .hasSalesDocument(false)
                .build();

        SuggestedConditionCodeKeyCombination suggestion = boReportService.suggestConditionCodeAndKeyCombination(condition);

        assertThat(suggestion.getConditionCode(), equalTo("ZPRK"));
        assertThat(suggestion.getKeyCombination(), equalTo("Rabatt per kunde/material"));
        assertThat(suggestion.getKeyCombinationTableName(), equalTo("A704"));
    }
}