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
import java.util.stream.Collectors;

import static no.ding.pk.repository.specifications.DiscountSpecifications.withMaterialNumber;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withSalesOrg;
import static no.ding.pk.repository.specifications.DiscountSpecifications.withZone;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
    private final String zone = "1";
    private final String materialDesignation = "Hageavfall, trær og røtter";
    private final double standardPrice = 1573.5;

    @Test
    public void shouldPersistAndGetDiscountAndDiscountLevelObjects() {
        List<Discount> actual = repository.findAll();
        assertThat(actual, hasSize(greaterThan(0)));
    }

    @Test
    public void shouldGetSpecificDiscount() {
        Discount actual = repository.findBySalesOrgAndMaterialNumberAndZone(salesOrg, materialNumber, zone);

        assertThat(actual, notNullValue());
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
        List<Discount> materialList = repository.findAllBySalesOrgAndZoneIsNullAndMaterialNumberIn(salesOrg, List.of("161201"));

        List<Discount> distinctList = materialList.stream().distinct().collect(Collectors.toList());

        assertThat(distinctList, hasSize(1));
    }

    @Test
    public void shouldGetDiscountForMaterialInListWithMultipleMaterials() {
        List<Discount> materialList = repository.findAllBySalesOrgAndZoneIsNullAndMaterialNumberIn(salesOrg, List.of("50106","50107","50108","50109"));

        List<Discount> distinctList = materialList.stream().distinct().collect(Collectors.toList());

        assertThat(distinctList, hasSize(4));
    }

    @Test
    public void shouldNotGetDiscountForMaterialWithZones() {
        List<Discount> materialList = repository.findAllBySalesOrgAndZoneIsNullAndMaterialNumberIn(salesOrg, List.of(materialNumber));

        assertThat(materialList, hasSize(0));
    }

    @Test
    public void shouldGetDiscountForMaterialWithZones() {
        List<Discount> discountList = repository.findAll(Specification.where(withSalesOrg(salesOrg).and(withZone(null)).and(withMaterialNumber(materialNumber))));

        assertThat(discountList, hasSize(greaterThan(0)));
    }

}
