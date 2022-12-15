package no.ding.pk.service.offer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;

@SpringBootTest
@TestPropertySource("/h2-db.properties")
public class MaterialServiceImplTest {

    @Autowired
    private MaterialService service;

    @Test
    void shouldPersistMaterialWithMaterialPrice() {
        MaterialPrice wastePrice = MaterialPrice.builder()
        .materialNumber("119901")
        .standardPrice(2456.00)
        .build();

        Material waste = Material.builder()
        .materialNumber("119901")
        .designation("Restavfall")
        .pricingUnit(1000)
        .quantumUnit("KG")
        .materialStandardPrice(wastePrice)
        .build();

        Material actual = service.save(waste);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getMaterialStandardPrice(), notNullValue());
        assertThat(actual.getMaterialStandardPrice().getId(), notNullValue());
    }
}
