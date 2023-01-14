package no.ding.pk.service.offer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
public class MaterialServiceImplTest {
    
    @Autowired
    private MaterialService service;
    
    @Autowired
    private MaterialPriceService mpService;
    
    @Test
    void shouldPersistMaterialWithMaterialPrice() {
        
        String materialNumber = "119901";
        
        MaterialPrice wastePrice = mpService.findByMaterialNumber(materialNumber);
        
        if(wastePrice == null) {
            wastePrice = MaterialPrice.builder()
            .materialNumber(materialNumber)
            .standardPrice(2456.00)
            .build();
            
            wastePrice = mpService.save(wastePrice);
        }
        
        Material waste = service.findByMaterialNumber(materialNumber);
        
        if(waste == null) {
            waste = Material.builder()
            .materialNumber(materialNumber)
            .designation("Restavfall")
            .priceUnit(1000)
            .quantumUnit("KG")
            .build();
        }

        waste.setMaterialStandardPrice(wastePrice);
        
        Material actual = service.save(waste);
        
        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getMaterialStandardPrice(), notNullValue());
        assertThat(actual.getMaterialStandardPrice().getId(), notNullValue());
    }
}
