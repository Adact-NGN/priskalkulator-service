package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.sap.SapMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PriceRowServiceImplUnitTest {

    private PriceRowService service;

    private PriceRowRepository repository;

    private MaterialService materialService;

    private MaterialPriceService materialPriceService;

    private SapMaterialService sapMaterialService;

    private ModelMapper modelMapper;

    private EntityManagerFactory emFactory;

    @BeforeEach
    public void setup() {
        repository = mock(PriceRowRepository.class);
        materialService = mock(MaterialService.class);
        materialPriceService = mock(MaterialPriceService.class);
        sapMaterialService = mock(SapMaterialService.class);
        emFactory = mock(EntityManagerFactory.class);
        modelMapper = new ModelMapper();

        service = new PriceRowServiceImpl(repository, materialService, materialPriceService, emFactory, sapMaterialService, modelMapper);
    }

    @Test
    public void shouldUpdateMaterialWithNewValues() {
        String materialNumber = "50103";

        MaterialPrice oldMaterialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(1817.0)
                .build();

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(oldMaterialPrice);

        Material oldMaterial = Material.builder()
                .id(1L)
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(oldMaterialPrice)
                .build();

        when(materialService.save(any())).thenAnswer(invocationOnMock -> {
            Material updatedMaterial = (Material) invocationOnMock.getArguments()[0];
            return updatedMaterial;
        });

        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        Query query = mock(Query.class);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(oldMaterial));
        when(entityManager.createNamedQuery(anyString())).thenReturn(query);
        when(emFactory.createEntityManager()).thenReturn(entityManager);

        PriceRow priceRow = PriceRow.builder()
                .material(oldMaterial)
                .standardPrice(1817.0)
                .discountLevel(3)
                .build();

        when(repository.save(any())).thenReturn(priceRow);

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(oldMaterialPrice);

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder()
                        .level(1)
                        .discount(0.0)
                        .build(),
                DiscountLevel.builder()
                        .level(2)
                        .discount(90.0)
                        .build(),
                DiscountLevel.builder()
                        .level(3)
                        .discount(180.0)
                        .build(),
                DiscountLevel.builder()
                        .level(4)
                        .discount(315.0)
                        .build(),
                DiscountLevel.builder()
                        .level(5)
                        .discount(468.0)
                        .build()
        );
        Discount discount = Discount.builder()
                .materialNumber(materialNumber)
                .discountLevels(discountLevels)
                .build();
        Map<String, Map<String, Map<String, Discount>>> discountMap = Map.of("100", Map.of("129", Map.of(materialNumber, discount)));

        MaterialPrice updatedMaterialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .pricingUnit(1)
                .standardPrice(1917.0)
                .build();

        Material updatedMaterial = Material.builder()
                .materialNumber(materialNumber)
                .pricingUnit(1)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(updatedMaterialPrice)
                .build();

        PriceRow updatedPriceRow = PriceRow.builder()
                .material(updatedMaterial)
                .standardPrice(1817.0)
                .discountLevel(3)
                .build();

        List<PriceRow> actual = service.saveAll(List.of(updatedPriceRow), "100", "129", "1", Map.of(updatedMaterialPrice.getMaterialNumber(), updatedMaterialPrice),
                discountMap);

        PriceRow actualPriceRow = actual.get(0);

        updatedMaterial.setId(1L);
        assertThat(actualPriceRow.getMaterial(), equalTo(updatedMaterial));
    }

    @Test
    public void shouldSetDiscountPctByStandardPriceAndDiscountLevelPrice() {
        String materialNumber = "50103";

        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(1817.0)
                .build();

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(materialPrice)
                .build();

        when(materialService.save(any())).thenReturn(material);

        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        Query query = mock(Query.class);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(material));
        when(entityManager.createNamedQuery(anyString())).thenReturn(query);
        when(emFactory.createEntityManager()).thenReturn(entityManager);

        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .standardPrice(1817.0)
                .discountLevel(3)
                .build();

        when(repository.save(any())).thenReturn(priceRow);

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder()
                        .level(1)
                        .discount(0.0)
                        .build(),
                DiscountLevel.builder()
                        .level(2)
                        .discount(90.0)
                        .build(),
                DiscountLevel.builder()
                        .level(3)
                        .discount(180.0)
                        .build(),
                DiscountLevel.builder()
                        .level(4)
                        .discount(315.0)
                        .build(),
                DiscountLevel.builder()
                        .level(5)
                        .discount(468.0)
                        .build()
        );
        Discount discount = Discount.builder()
                .materialNumber(materialNumber)
                .discountLevels(discountLevels)
                .build();
        Map<String, Map<String, Map<String, Discount>>> discountMap = Map.of("100", Map.of("129", Map.of(materialNumber, discount)));

        List<PriceRow> actual = service.saveAll(List.of(priceRow), "100", "129", "1", Map.of(materialPrice.getMaterialNumber(), materialPrice),
                discountMap);

        PriceRow actualPriceRow = actual.get(0);

        assertThat(actualPriceRow.getDiscountedPrice(), lessThan(actualPriceRow.getStandardPrice()));

        double expectedDiscountPercentage = 180.0 / priceRow.getStandardPrice();

        assertThat(actualPriceRow.getDiscountLevelPct(), equalTo(expectedDiscountPercentage));

        double expectedDiscountedPrice = priceRow.getStandardPrice() - 180.0;

        assertThat(actualPriceRow.getDiscountedPrice(), equalTo(expectedDiscountedPrice));

        assertThat(actualPriceRow.getDiscountLevelPct(), notNullValue());
    }

    @Test
    public void shouldSetDiscountLevelToOneWhenManualPriceIsSet() {
        String materialNumber = "50103";

        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(1817.0)
                .build();

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(materialPrice)
                .build();

        when(materialService.save(any())).thenReturn(material);

        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        Query query = mock(Query.class);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(material));
        when(entityManager.createNamedQuery(anyString())).thenReturn(query);
        when(emFactory.createEntityManager()).thenReturn(entityManager);

        double standardPrice = 1817.0;
        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .standardPrice(standardPrice)
                .manualPrice(standardPrice-0.0)
                .build();

        when(repository.save(any())).thenReturn(priceRow);

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder()
                        .level(1)
                        .discount(0.0)
                        .build(),
                DiscountLevel.builder()
                        .level(2)
                        .discount(90.0)
                        .build(),
                DiscountLevel.builder()
                        .level(3)
                        .discount(180.0)
                        .build(),
                DiscountLevel.builder()
                        .level(4)
                        .discount(315.0)
                        .build(),
                DiscountLevel.builder()
                        .level(5)
                        .discount(468.0)
                        .build()
        );
        Discount discount = Discount.builder()
                .materialNumber(materialNumber)
                .discountLevels(discountLevels)
                .build();
        Map<String, Map<String, Map<String, Discount>>> discountMap = Map.of("100", Map.of("129", Map.of(materialNumber, discount)));

        List<PriceRow> actual = service.saveAll(List.of(priceRow), "100", "129", "1", Map.of(materialPrice.getMaterialNumber(), materialPrice),
                discountMap);

        assertThat(actual.get(0).getDiscountLevel(), is(1));
    }

    @Test
    public void shouldSetDiscountLevelToTwoWhenManualPriceIsSet() {
        String materialNumber = "50103";

        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(1817.0)
                .build();

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(materialPrice)
                .build();

        when(materialService.save(any())).thenReturn(material);

        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        Query query = mock(Query.class);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(material));
        when(entityManager.createNamedQuery(anyString())).thenReturn(query);
        when(emFactory.createEntityManager()).thenReturn(entityManager);

        double standardPrice = 1817.0;
        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .standardPrice(standardPrice)
                .manualPrice(standardPrice-80.0)
                .build();

        when(repository.save(any())).thenReturn(priceRow);

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder()
                        .level(1)
                        .discount(0.0)
                        .build(),
                DiscountLevel.builder()
                        .level(2)
                        .discount(90.0)
                        .build(),
                DiscountLevel.builder()
                        .level(3)
                        .discount(180.0)
                        .build(),
                DiscountLevel.builder()
                        .level(4)
                        .discount(315.0)
                        .build(),
                DiscountLevel.builder()
                        .level(5)
                        .discount(468.0)
                        .build()
        );
        Discount discount = Discount.builder()
                .materialNumber(materialNumber)
                .discountLevels(discountLevels)
                .build();
        Map<String, Map<String, Map<String, Discount>>> discountMap = Map.of("100", Map.of("129", Map.of(materialNumber, discount)));

        List<PriceRow> actual = service.saveAll(List.of(priceRow), "100", "129", "1", Map.of(materialPrice.getMaterialNumber(), materialPrice),
                discountMap);

        assertThat(actual.get(0).getDiscountLevel(), is(2));
    }

    @Test
    public void shouldSetDiscountLevelAboveFiveWhenManualPriceIsSet() {
        String materialNumber = "50103";

        MaterialPrice materialPrice = MaterialPrice.builder()
                .materialNumber(materialNumber)
                .standardPrice(1817.0)
                .build();

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(materialPrice)
                .build();

        when(materialService.save(any())).thenReturn(material);

        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        Query query = mock(Query.class);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(material));
        when(entityManager.createNamedQuery(anyString())).thenReturn(query);
        when(emFactory.createEntityManager()).thenReturn(entityManager);

        double standardPrice = 1817.0;
        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .standardPrice(standardPrice)
                .manualPrice(standardPrice-500.0)
                .build();

        when(repository.save(any())).thenReturn(priceRow);

        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder()
                        .level(1)
                        .discount(0.0)
                        .build(),
                DiscountLevel.builder()
                        .level(2)
                        .discount(90.0)
                        .build(),
                DiscountLevel.builder()
                        .level(3)
                        .discount(180.0)
                        .build(),
                DiscountLevel.builder()
                        .level(4)
                        .discount(315.0)
                        .build(),
                DiscountLevel.builder()
                        .level(5)
                        .discount(468.0)
                        .build()
        );
        Discount discount = Discount.builder()
                .materialNumber(materialNumber)
                .discountLevels(discountLevels)
                .build();
        Map<String, Map<String, Map<String, Discount>>> discountMap = Map.of("100", Map.of("129", Map.of(materialNumber, discount)));

        List<PriceRow> actual = service.saveAll(List.of(priceRow), "100", "129", "1", Map.of(materialPrice.getMaterialNumber(), materialPrice),
                discountMap);

        assertThat(actual.get(0).getDiscountLevel(), is(6));
    }
}
