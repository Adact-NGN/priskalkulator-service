package no.ding.pk.service.offer;

import no.ding.pk.config.AbstractIntegrationConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.DiscountService;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static no.ding.pk.utils.JsonTestUtils.mockSapMaterialServiceResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Import(ModelMapperV2Config.class)
public class PriceRowServiceImplTest extends AbstractIntegrationConfig {

    private PriceRowService service;

    @Autowired
    private PriceRowRepository priceRowRepository;

    @MockBean
    private DiscountService discountService;

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
        service = new PriceRowServiceImpl(
                discountService,
                priceRowRepository,
                materialService,
                materialPriceService,
                emFactory,
                sapMaterialService,
                modelMapper);

        ClassLoader classLoader = getClass().getClassLoader();
        List<MaterialDTO> sapMaterialDTOS = mockSapMaterialServiceResponse(classLoader);

        doReturn(sapMaterialDTOS).when(sapMaterialService).getAllMaterialsForSalesOrgByZone(anyString(), anyInt(), any());
    }

    @Test
    public void shouldSaveZonedPrice() {
        String zoneMaterialNumber = "50101";

        Material listPlacement = Material.builder()
                .materialNumber(zoneMaterialNumber)
                .designation("Lift - Utsett")
                .pricingUnit(1)
                .quantumUnit("ST")
                .salesZone("0000000001")
                .build();

        PriceRow wastePriceRow = PriceRow.builder()
                .standardPrice(1599.0)
                .material(listPlacement)
                .showPriceInOffer(true)
                .discountLevel(2)
                .discountLevelPrice(56.0)
                .amount(1)
                .build();

        List<PriceRow> wastePriceRowList = new ArrayList<>();
        wastePriceRowList.add(wastePriceRow);

        DiscountLevel levelOne = DiscountLevel.builder()
                .level(1)
                .discount(0.0)
                .pctDiscount(0.0)
                .zone(1)
                .build();

        when(discountService.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel("100", "104", zoneMaterialNumber, 1, 1)).thenReturn(Collections.singletonList(levelOne));

        DiscountLevel levelTwo = DiscountLevel.builder()
                .level(2)
                .discount(189.0)
                .zone(1)
                .build();

        when(discountService.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel("100", "104", zoneMaterialNumber, 2, 1)).thenReturn(Collections.singletonList(levelTwo));

        List<DiscountLevel> discountLevels = new LinkedList<>();
        discountLevels.add(levelOne);
        discountLevels.add(levelTwo);

        Discount discount = Discount.builder()
                .materialNumber(zoneMaterialNumber)
                .standardPrice(1599.0)
                .salesOffice("104")
                .salesOrg("100")
                .materialDesignation("Lift - Utsett")
                .discountLevels(discountLevels)
                .build();

        Map<String, Map<String, Map<String, Discount>>> discountMap = new HashMap<>();
        discountMap.put("100", Map.of("104", Map.of("50101", discount)));

        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber("50101")
                .pricingUnit(1)
                .standardPrice(1599.0)
                .quantumUnit("ST")
                .build();
        Map<String, MaterialPrice> materialStdPrice = new HashMap<>();
        materialStdPrice.put("50101_01", materialPrice);

        List<PriceRow> priceRows = service.saveAll(wastePriceRowList, "100", "104", "0000000001", materialStdPrice);

        assertThat(priceRows, notNullValue());
        assertThat(priceRows, not(empty()));
        assertThat(priceRows.get(0).getDiscountedPrice(), is(materialPrice.getStandardPrice() - levelTwo.getDiscount()));
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

        List<PriceRow> priceRows = service.saveAll(wastePriceRowList, "100", "104", new HashMap<>());

        assertThat(priceRows, notNullValue());
        assertThat(priceRows, not(empty()));
    }
}
