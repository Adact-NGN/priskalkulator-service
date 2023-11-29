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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@Disabled("SAP is down")
@Profile("itest")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SapMaterialServiceImplITest {

    @Autowired
    private SapMaterialService service;

    @Test
    public void shouldGetMaterialsFromSAPByMaterialNumber() {
        List<MaterialDTO> materialsWithStdPrice = service.getAllMaterialsForSalesOrgBy("100", null, 0, 5000);

        assertThat(materialsWithStdPrice, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetMaterialWithStdPriceForZones() {
        List<MaterialDTO> materialsWithStdPriceForZone = service.getAllMaterialsForSalesOrgBy("100", "02",0, 5000);

        assertThat(materialsWithStdPriceForZone, hasSize(greaterThan(0)));
    }
}