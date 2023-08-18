
package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.BoReportCondition;
import no.ding.pk.domain.bo.SuggestedConditionCodeKeyCombination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class BoReportConditionCodeServiceImplTest {

    @Autowired
    private BoReportConditionCodeService boReportService;

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