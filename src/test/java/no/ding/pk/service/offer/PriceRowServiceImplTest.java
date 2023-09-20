package no.ding.pk.service.offer;

import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.sap.SapMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

//@SpringBootTest
//@Transactional
//@TestPropertySource("/h2-db.properties")
@Disabled
@Import(ModelMapperV2Config.class)
public class PriceRowServiceImplTest extends AbstractIntegrationConfig {

    private PriceRowService service;

    @Autowired
    private PriceRowRepository priceRowRepository;

    @MockBean
    private MaterialService materialService;

    @MockBean
    private MaterialPriceService materialPriceService;

    @Autowired
    private EntityManagerFactory emFactory;

    @MockBean
    private SapMaterialService sapMaterialService;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        service = new PriceRowServiceImpl(priceRowRepository,
                materialService,
                materialPriceService,
                emFactory,
                sapMaterialService,
                modelMapper);
    }

    @Test
    void testSaveAll() {

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
