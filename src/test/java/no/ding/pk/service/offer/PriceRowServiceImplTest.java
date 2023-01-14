package no.ding.pk.service.offer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
public class PriceRowServiceImplTest {

    @Autowired
    private PriceRowService service;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialPriceService mpService;

    @Test
    void testSaveAll() {

        String materialNumber = "119901";

        MaterialPrice wastePrice = mpService.findByMaterialNumber(materialNumber);
        
        if(wastePrice == null) {
            wastePrice = MaterialPrice.builder()
            .materialNumber(materialNumber)
            .standardPrice(2456.00)
            .build();
        }

        Material waste = materialService.findByMaterialNumber(materialNumber);

        if(waste == null) {
            waste = Material.builder()
            .materialNumber(materialNumber)
            .designation("Restavfall")
            .priceUnit(1000)
            .quantumUnit("KG")
            .materialStandardPrice(wastePrice)
            .build();
        }

        PriceRow wastePriceRow = PriceRow.builder()
        .customerPrice(2456.0)
        .discountPct(0.02)
        .material(waste)
        .showPriceInOffer(true)
        .manualPrice(2400.0)
        .discountLevel(1)
        .discountLevelPrice(56.0)
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
