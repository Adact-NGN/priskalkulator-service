package no.ding.pk.repository.offer;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource("classpath:h2-db.properties")
class PriceRowRepositoryTest {

    @Autowired
    private PriceRowRepository repository;

    @Test
    public void shouldPersistPriceRow() {
        String materialNumber = "119901";
        MaterialPrice standardPrice = MaterialPrice.builder("100", "100", materialNumber, null, "01")
                .standardPrice(2604.0)
                .pricingUnit(1000)
                .quantumUnit("KG")
                .build();
        Material material = Material.builder()
                .materialNumber(materialNumber)
                .materialStandardPrice(standardPrice)
                .pricingUnit(1000)
                .quantumUnit("KG")
                .build();
        PriceRow priceRow = PriceRow.builder()
                .showPriceInOffer(true)
                .needsApproval(false)
                .material(material)
                .manualPrice(1.0)
                .standardPrice(2604.0)
                .categoryId("00300")
                .categoryDescription("Avafall")
                .subCategoryId("0030000100")
                .subCategoryDescription("Blandet avfall")
                .build();

        priceRow = repository.save(priceRow);
    }
}