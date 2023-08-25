package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.ConditionCode;
import no.ding.pk.domain.bo.KeyCombination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest()
@TestPropertySource("/h2-db.properties")
@Sql(value = {
        "/conditional_code_key_combination_scripts/drop_schemas.sql",
        "/conditional_code_key_combination_scripts/create_condition_code.sql",
        "/conditional_code_key_combination_scripts/create_key_combination.sql"
})
@Sql(value = {
        "/conditional_code_key_combination_scripts/insert_condition_code_with_key_combination.sql"
})
public class BoReportConditionCodeServiceImplTest {

    @Autowired
    private BoReportConditionCodeService service;

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
}
