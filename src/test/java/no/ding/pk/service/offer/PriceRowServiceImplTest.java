package no.ding.pk.service.offer;

import no.ding.pk.config.AbstractIntegrationConfig;
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

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.*;

import static no.ding.pk.utils.JsonTestUtils.mockSapMaterialServiceResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class PriceRowServiceImplTest extends AbstractIntegrationConfig {

    private PriceRowService service;

    @Autowired
    private PriceRowRepository priceRowRepository;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialPriceService materialPriceService;

    @Autowired
    private EntityManagerFactory emFactory;

    @MockBean
    private SapMaterialService sapMaterialService;

    @Autowired
    @Qualifier("modelMapperV2")
    private ModelMapper modelMapper;
    private List<MaterialDTO> sapMaterialDTOS;

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
        sapMaterialDTOS = mockSapMaterialServiceResponse(classLoader);

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

        DiscountLevel levelOne = DiscountLevel.builder(0.0, 1)
                .pctDiscount(0.0)
                .zone(1)
                .build();

        String salesOrg = "100";
        String salesOffice = "104";
        when(discountService.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, salesOffice,
                zoneMaterialNumber, 1, 1)).thenReturn(Collections.singletonList(levelOne));

        DiscountLevel levelTwo = DiscountLevel.builder(189.0, 2)
                .zone(1)
                .build();

        when(discountService.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, salesOffice,
                zoneMaterialNumber, 2, 1)).thenReturn(Collections.singletonList(levelTwo));

        MaterialPrice materialPrice = MaterialPrice.builder("100", "100", "50101", null, "01")
                .pricingUnit(1)
                .standardPrice(1599.0)
                .quantumUnit("ST")
                .build();
        Map<String, MaterialPrice> materialStdPrice = new HashMap<>();
        materialStdPrice.put("50101_01", materialPrice);

        List<PriceRow> priceRows = service.saveAll(wastePriceRowList, salesOrg, salesOffice, "0000000001", materialStdPrice);

        assertThat(priceRows, notNullValue());
        assertThat(priceRows, not(empty()));
        assertThat(priceRows.get(0).getDiscountedPrice(), is(materialPrice.getStandardPrice() - levelTwo.getDiscount()));
    }

    @Test
    void testSaveAll() {

        String materialNumber = "119901";

        MaterialPrice wastePrice = MaterialPrice.builder("100", "100", materialNumber, null, "01")
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

    @Test
    void testSaveAllWithExistingMaterialPrice() {

        String materialNumber = "119901";

        Optional<MaterialDTO> dtoOptional = sapMaterialDTOS.stream().filter(materialDTO -> materialDTO.getMaterial().equals(materialNumber)).findAny();

        assertThat(dtoOptional.isPresent(), is(true));

        String salesOrg = "100";
        String salesOffice = "104";
        when(sapMaterialService.getMaterialByMaterialNumberAndSalesOrgAndSalesOffice("119901", salesOrg, salesOffice, null)).thenReturn(dtoOptional.get());

        MaterialPrice wastePrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
                .materialNumber(materialNumber)
                .standardPrice(2456.00)
                .build();

        wastePrice = materialPriceService.save(wastePrice);

        MaterialPrice standardPrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
                .standardPrice(2456.00)
                .build();
        Material waste = Material.builder()
                .materialNumber(materialNumber)
                .designation("Restavfall")
                .pricingUnit(1000)
                .quantumUnit("KG")
                .materialStandardPrice(standardPrice)
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

        List<PriceRow> priceRows = service.saveAll(wastePriceRowList, salesOrg, salesOffice, Map.of("119901", standardPrice));

        assertThat(priceRows, notNullValue());
        assertThat(priceRows, not(empty()));
        assertThat(priceRows.get(0).getMaterial().getMaterialStandardPrice(), equalTo(wastePrice));
    }
}
