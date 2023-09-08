package no.ding.pk.service.offer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static reactor.core.publisher.Mono.when;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.sap.SapMaterialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;

//@SpringBootTest
//@Transactional
//@TestPropertySource("/h2-db.properties")
@ExtendWith(MockitoExtension.class)
public class PriceRowServiceImplTest {


    private PriceRowService service;

    @Mock
    private MaterialService materialService;
    @Mock
    private MaterialPriceService mpService;
    @Mock
    private PriceRowRepository priceRowRepository;
    @Mock
    private MaterialPriceService materialPriceService;
    @Mock
    private EntityManagerFactory emFactory;
    @Mock
    private SapMaterialService sapMaterialService;

    @Test
    void testSaveAll() {

        service = new PriceRowServiceImpl(priceRowRepository, materialService, materialPriceService, emFactory,
                sapMaterialService, new ModelMapper());

        String materialNumber = "119901";


        MaterialPrice wastePrice = MaterialPrice.builder()
            .materialNumber(materialNumber)
            .standardPrice(2456.00)
            .build();

        Material waste = Material.builder()
            .materialNumber(materialNumber)
            .designation("Restavfall")
            .pricingUnit(1000)
            .quantumUnit("KG")
            .materialStandardPrice(wastePrice)
            .build();

        PriceRow wastePriceRow = PriceRow.builder()
        .customerPrice(2456.0)
        .discountLevelPct(0.02)
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

        List<PriceRow> priceRows = service.saveAll(wastePriceRowList, "100", "104", new ArrayList<>(), null);

        assertThat(priceRows, notNullValue());
        assertThat(priceRows, not(empty()));
    }
}
