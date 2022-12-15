package no.ding.pk.service.offer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;

@SpringBootTest
@TestPropertySource("/h2-db.properties")
public class PriceRowServiceImplTest {

    @Autowired
    private PriceRowService service;

    @Test
    void testSaveAll() {

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

        PriceRow wastePriceRow = PriceRow.builder()
        .customerPrice(2456.0)
        .discountPct(0.02)
        .material(waste)
        .showPriceInOffer(true)
        .manualPrice(2400.0)
        .priceLevel(1)
        .priceLevelPrice(56.0)
        .amount(1)
        .priceIncMva(2448.0)
        .build();

        List<PriceRow> wastePriceRowList = new ArrayList<>();
        wastePriceRowList.add(wastePriceRow);

        List<PriceRow> priceRows = service.saveAll(wastePriceRowList);

        assertThat(priceRows, notNullValue());
        assertThat(priceRows, not(empty()));
    }
}
