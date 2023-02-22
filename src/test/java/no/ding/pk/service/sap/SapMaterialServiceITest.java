package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.MaterialDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@Disabled
@Profile("itest")
@SpringBootTest
public class SapMaterialServiceITest {
    
    @Autowired
    private SapMaterialService sapMaterialService;

    @Test
    public void shouldGetAListOfMaterialsFromSapMaterialService() {
        List<MaterialDTO> materialDTOs = sapMaterialService.getAllMaterialsForSalesOrg("100", 0, 100);

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
