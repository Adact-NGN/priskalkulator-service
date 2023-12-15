package no.ding.pk.service.offer;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.service.DiscountService;
import no.ding.pk.service.sap.SapMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
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

    private DiscountService discountService;

    @BeforeEach
    public void setup() {
        repository = mock(PriceRowRepository.class);
        materialService = mock(MaterialService.class);
        materialPriceService = mock(MaterialPriceService.class);
        sapMaterialService = mock(SapMaterialService.class);
        emFactory = mock(EntityManagerFactory.class);
        modelMapper = new ModelMapper();
        discountService = mock(DiscountService.class);

        service = new PriceRowServiceImpl(discountService, repository, materialService, emFactory, sapMaterialService, modelMapper);
    }

    @Test
    public void shouldUpdateMaterialWithNewValues() {
        String materialNumber = "50103";
        String salesOrg = "100";
        String salesOffice = "129";

        MaterialPrice oldMaterialPrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
                .standardPrice(1817.0)
                .build();

        doReturn(Optional.ofNullable(oldMaterialPrice)).when(materialPriceService).findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                anyString(), anyString(), anyString(), any(), anyString());

        Material oldMaterial = Material.builder()
                .id(1L)
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(oldMaterialPrice)
                .build();

        when(materialService.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

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
                DiscountLevel.builder(0.0, 1)
                        .build(),
                DiscountLevel.builder(90.0, 2)
                        .build(),
                DiscountLevel.builder(180.0, 3)
                        .build(),
                DiscountLevel.builder(315.0, 4)
                        .build(),
                DiscountLevel.builder(468.0, 5)
                        .build()
        );



        MaterialPrice updatedMaterialPrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
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

        List<PriceRow> actual = service.saveAll(List.of(updatedPriceRow), salesOrg, salesOffice, "1",
                Map.of(updatedMaterialPrice.getMaterialNumber(), updatedMaterialPrice));

        PriceRow actualPriceRow = actual.get(0);

        updatedMaterial.setId(1L);
        assertThat(actualPriceRow.getMaterial(), equalTo(updatedMaterial));
    }

    @Test
    public void shouldSetDiscountPctByStandardPriceAndDiscountLevelPrice() {
        String materialNumber = "50103";

        MaterialPrice materialPrice = MaterialPrice.builder("100", "100", materialNumber, null, "01")
                .standardPrice(1817.0)
                .build();

//        doReturn(materialPrice).when(materialPriceService).findByMaterialNumber(anyString());
        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));
        Material material = Material.builder()
                .materialNumber(materialNumber)
                .designation("Lift - Tømming")
                .materialGroupDesignation("Tj. Lift")
                .materialTypeDescription("Tjeneste")
                .materialStandardPrice(materialPrice)
                .build();

        doReturn(material).when(materialService).save(any());

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

//        when(repository.save(any())).thenReturn(priceRow);

//        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);
        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder(0.0, 1)
                        .zone(1)
                        .build(),
                DiscountLevel.builder(90.0, 2)
                        .zone(1)
                        .build(),
                DiscountLevel.builder(180.0, 3)
                        .zone(1)
                        .build(),
                DiscountLevel.builder(315.0, 4)
                        .zone(1)
                        .build(),
                DiscountLevel.builder(468.0, 5)
                        .zone(1)
                        .build()
        );

        String salesOrg = "100";
        String salesOffice = "129";
        when(discountService.findDiscountLevelsBySalesOrgAndMaterialNumberAndDiscountLevel(salesOrg, salesOffice,
                materialNumber, 3, 1)).thenReturn(Collections.singletonList(discountLevels.get(2)));

        Discount discount = Discount.builder(salesOrg, salesOffice, materialNumber, 1817.0, discountLevels)
                .build();

        when(discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(salesOrg, salesOffice,
                Collections.singletonList(materialNumber))).thenReturn(Collections.singletonList(discount));

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        List<PriceRow> actual = service.saveAll(List.of(priceRow), salesOrg, salesOffice, "1",
                Map.of(materialPrice.getMaterialNumber(), materialPrice));

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
        String salesOrg = "100";
        String salesOffice = "129";

        MaterialPrice materialPrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
                .standardPrice(1817.0)
                .build();

//        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);
        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));

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

//        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);
        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder(0.0, 1)
                        .build(),
                DiscountLevel.builder(90.0, 2)
                        .build(),
                DiscountLevel.builder(180.0, 3)
                        .build(),
                DiscountLevel.builder(315.0, 4)
                        .build(),
                DiscountLevel.builder(468.0, 5)
                        .build()
        );


        Discount discount = Discount.builder(salesOrg, salesOffice, materialNumber, standardPrice, discountLevels)
                .build();

        when(discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(salesOrg, salesOffice, Collections.singletonList(materialNumber))).thenReturn(Collections.singletonList(discount));

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        List<PriceRow> actual = service.saveAll(List.of(priceRow), salesOrg, salesOffice, "1",
                Map.of(materialPrice.getMaterialNumber(), materialPrice));

        assertThat(actual.get(0).getDiscountLevel(), is(1));
    }

    @Test
    public void shouldSetDiscountLevelToTwoWhenManualPriceIsSet() {
        String materialNumber = "50103";
        String salesOrg = "100";
        String salesOffice = "129";

        MaterialPrice materialPrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
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

//        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);
        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder(0.0, 1)
                        .build(),
                DiscountLevel.builder(90.0, 2)
                        .build(),
                DiscountLevel.builder(180.0, 3)
                        .build(),
                DiscountLevel.builder(315.0, 4)
                        .build(),
                DiscountLevel.builder(468.0, 5)
                        .build()
        );

        Discount discount = Discount.builder(salesOrg, salesOffice, materialNumber, standardPrice, discountLevels)
                .build();

        when(discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(salesOrg, salesOffice, Collections.singletonList(materialNumber))).thenReturn(Collections.singletonList(discount));

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        List<PriceRow> actual = service.saveAll(List.of(priceRow), salesOrg, salesOffice, "1",
                Map.of(materialPrice.getMaterialNumber(), materialPrice));

        assertThat(actual.get(0).getDiscountLevel(), is(2));
    }

    @Test
    public void shouldSetDiscountLevelAboveFiveWhenManualPriceIsSet() {
        String salesOrg = "100";
        String salesOffice = "129";
        String materialNumber = "50103";

        MaterialPrice materialPrice = MaterialPrice.builder(salesOrg, salesOffice, materialNumber, null, "01")
                .standardPrice(1817.0)
                .build();

        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));

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

//        when(materialPriceService.findByMaterialNumber(anyString())).thenReturn(materialPrice);
        when(materialPriceService
                .findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(
                        anyString(), anyString(), anyString(), any(), anyString()
                )).thenReturn(Optional.ofNullable(materialPrice));

        List<DiscountLevel> discountLevels = List.of(
                DiscountLevel.builder(0.0, 1)
                        .build(),
                DiscountLevel.builder(90.0, 2)
                        .build(),
                DiscountLevel.builder(180.0, 3)
                        .build(),
                DiscountLevel.builder(315.0, 4)
                        .build(),
                DiscountLevel.builder(468.0, 5)
                        .build()
        );

        Discount discount = Discount.builder(salesOrg, salesOffice, materialNumber, standardPrice, discountLevels)
                .build();

        List<String> materials = new ArrayList<>();
        materials.add(materialNumber);

        when(discountService.findAllDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn(salesOrg, salesOffice, materials)).thenReturn(Collections.singletonList(discount));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        List<PriceRow> actual = service.saveAll(List.of(priceRow), salesOrg, salesOffice, "1",
                Map.of(materialPrice.getMaterialNumber(), materialPrice));

        assertThat(actual.get(0).getDiscountLevel(), is(6));
    }
}
