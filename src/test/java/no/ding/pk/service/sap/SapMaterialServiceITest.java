package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.MaterialDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@Disabled("SAP is down")
@Profile("itest")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class SapMaterialServiceITest {
    
    @Autowired
    private SapMaterialService sapMaterialService;

    @Test
    public void shouldGetAListOfMaterialsFromSapMaterialService() {
        List<MaterialDTO> materialDTOs = sapMaterialService.getAllMaterialsForSalesOrgByZone("100", 0, 100);

        assertThat(materialDTOs, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetMaterialByMaterialNumberFromSapMaterialService() {
        String materialNumber = "119901";
        MaterialDTO materialDto = sapMaterialService.getMaterialByMaterialNumberAndSalesOrg(materialNumber, "100");

        // assertThat(materialDto, hasSize(greaterThan(0)));
        assertThat(materialDto.getMaterial(), equalTo(materialNumber));
    }
    
}
