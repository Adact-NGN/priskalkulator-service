package no.ding.pk.repository;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.ding.pk.repository.specifications.DiscountSpecifications.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
@SqlConfig(commentPrefix = "#")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(value = {"/discout_db_scripts/drop_schema.sql", "/discout_db_scripts/create_schema.sql"})
@Sql(value = {"/discout_db_scripts/discount_matrix.sql", "/discout_db_scripts/discount_levels.sql"})
public class DiscountRepositoryTest {

    @Autowired
    private DiscountRepository repository;

    @Autowired
    private DiscountLevelRepository discountLevelRepository;

    private final String salesOrg = "100";
    private final String salesOffice = "100";
    private final String materialNumber = "50101";

    @Test
    public void shouldPersistAndGetDiscountAndDiscountLevelObjects() {
        List<Discount> actual = repository.findAll();
        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetSpecificDiscount() {
        Specification<Discount> specification = withSalesOrg(salesOrg).and(withSalesOffice(salesOffice)).and(withMaterialNumber(materialNumber));
        Optional<Discount> actual = repository.findOne(specification);

        assertThat(actual.isPresent(), is(true));
    }

    @Test
    public void shouldGetSpecificDiscountLevel() {
        List<DiscountLevel> dls = discountLevelRepository.findAll();

        assertThat(dls, hasSize(greaterThan(0)));

        List<DiscountLevel> actualList = discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumberAndLevel(salesOrg, materialNumber, 2);

        assertThat(actualList, hasSize(greaterThan(0)));
        assertThat(actualList.get(0).getLevel(), is(2));
    }

    @Test
    public void shouldGetAllDiscountsForSalesOrg() {
        List<Discount> actual = repository.findAllBySalesOrg(salesOrg);

        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetDiscountForMaterialInListWithSingleMaterial() {
        List<Discount> materialList = repository.findAllBySalesOrgAndDiscountLevelsZoneIsNullAndMaterialNumberIn(salesOrg, List.of("161201"));

        List<Discount> distinctList = materialList.stream().distinct().collect(Collectors.toList());

        assertThat(distinctList, hasSize(1));
    }

    @Test
    public void shouldGetDiscountForMaterialInListWithMultipleMaterials() {
        List<Discount> materialList = repository.findAllBySalesOrgAndDiscountLevelsZoneIsNullAndMaterialNumberIn(salesOrg, List.of("50106","50107","50108","50109"));

        List<Discount> distinctList = materialList.stream().distinct().collect(Collectors.toList());

        assertThat(distinctList, hasSize(4));
    }

    @Test
    public void shouldNotGetDiscountForMaterialWithZones() {
        List<Discount> materialList = repository.findAllBySalesOrgAndDiscountLevelsZoneIsNullAndMaterialNumberIn(salesOrg, List.of(materialNumber));

        assertThat(materialList, hasSize(0));
    }

    @Test
    public void shouldGetDiscountForMaterialWithZones() {

        Integer zone = 1;
        Specification<Discount> specification = withSalesOrg(salesOrg).and(withSalesOffice(salesOffice)).and(withMaterialNumber(materialNumber).and(hasDiscountLevelInZone(zone)));
        List<Discount> discountList = repository.findAll(specification);

        assertThat(discountList, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetDiscountForMaterialWithoutZones() {
        Specification<Discount> specification = withSalesOrg("100").and(withSalesOffice("100")).and(withMaterialNumber("C-02L")).and(hasDiscountLevelInZone(null));
        List<Discount> discountList = repository.findAll(specification);

        assertThat(discountList, hasSize(greaterThan(0)));
    }
}
