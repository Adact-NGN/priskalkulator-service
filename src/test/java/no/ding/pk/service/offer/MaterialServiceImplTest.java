package no.ding.pk.service.offer;

import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;

@Disabled
public class MaterialServiceImplTest extends AbstractIntegrationConfig {
    
    private MaterialService service;

    @MockBean
    private MaterialPriceService materialPriceService;

    @Autowired
    private MaterialRepository materialRepository;

    @BeforeEach
    public void setup() {
        service = new MaterialServiceImpl(materialRepository, materialPriceService);
    }

    private void createMaterial() {
        String materialNumber = "119901";

        MaterialPrice wastePrice = MaterialPrice.builder("100", "100", materialNumber, null, null)
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
        
        Optional<Material> actual = service.findByMaterialNumber(materialNumber);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get().getId(), notNullValue());
        assertThat(actual.get().getMaterialStandardPrice(), notNullValue());
        assertThat(actual.get().getMaterialStandardPrice().getId(), notNullValue());
    }
}
