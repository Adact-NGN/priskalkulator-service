package no.ding.pk.service.offer;

import jakarta.transaction.Transactional;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
public class MaterialServiceImplTest {
    
    @Autowired
    private MaterialService service;

    private void createMaterial() {
        String materialNumber = "119901";

        MaterialPrice wastePrice = MaterialPrice.builder()
        .materialNumber(materialNumber)
        .standardPrice(2456.00)
        .build();
        
        Material waste = Material.builder()
        .designation("Restavfall")
        .materialNumber(materialNumber)
        .pricingUnit(1000)
        .quantumUnit("KG")
        .materialGroup("9912")
        .materialGroupDesignation("Bl. næringsavfall")
        .materialType("ZWAF")
        .materialTypeDescription("Avfallsmateriale")
        .materialStandardPrice(wastePrice)
        .build();

        service.save(waste);
    }
    
    @Test
    void shouldPersistMaterialWithMaterialPrice() {

        createMaterial();
        
        String materialNumber = "119901";
        
        Material actual = service.findByMaterialNumber(materialNumber);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getMaterialStandardPrice(), notNullValue());
        assertThat(actual.getMaterialStandardPrice().getId(), notNullValue());
    }
}
