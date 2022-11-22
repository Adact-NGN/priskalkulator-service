package no.ding.pk.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;

@Transactional
@SpringBootTest
@TestPropertySource("/h2-db.properties")
public class DiscountRepositoryTest {

    @Autowired
    private DiscountRepository repository;

    @Autowired
    private DiscountLevelRepository discountLevelRepository;

    private String salesOrg = "100";
    private String salesOffice = "100";
    private String materialNumber = "113103";
    private String materialDesignation = "Hageavfall, trær og røtter";
    private double standardPrice = 1573.5;

    @Test
    public void shouldPersistAndGetDicountAndDiscountLevelObjects() {
        Discount expected = creatDiscountObjectWithLevels(salesOrg, 
        materialNumber, 
        materialDesignation,
        salesOffice,
        standardPrice);

        repository.save(expected);

        List<Discount> actual = repository.findAll();
        assertThat(actual, hasSize(1));
    }

    @Test
    public void shouldGetSpecificDiscount() {
        
        Discount expected = creatDiscountObjectWithLevels(salesOrg, 
        materialNumber, 
        materialDesignation,
        salesOffice,
        standardPrice);

        repository.save(expected);

        Discount actual = repository.findBySalesOrgAndMaterialNumber(salesOrg, materialNumber);

        assertThat(actual, notNullValue());
    }

    @Test
    public void shouldGetSpecificDiscountLevel() {
        Discount expected = creatDiscountObjectWithLevels(salesOrg, 
        materialNumber, 
        materialDesignation,
        salesOffice,
        standardPrice);

        repository.save(expected);

        List<DiscountLevel> dls = discountLevelRepository.findAll();

        assertThat(dls, hasSize(5));

        List<DiscountLevel> actualList = discountLevelRepository.findAllByParentSalesOrgAndParentMaterialNumberAndLevel(salesOrg, materialNumber, 2);

        assertThat(actualList, hasSize(1));
        assertThat(actualList.get(0).getLevel(), is(2));
    }

    @Test
    public void shouldGetAllDiscountsForSalsOrg() {
        repository.save(creatDiscountObjectWithLevels(salesOrg, materialNumber, materialDesignation, salesOffice, standardPrice));
        repository.save(creatDiscountObjectWithLevels("100", "100", "Hageavfall, kvernet", "100", 1194.5));

        List<Discount> actual = repository.findAllBySalesOrg(salesOrg);

        assertThat(actual, hasSize(2));
    }

    private Discount creatDiscountObjectWithLevels(String salesOrg, String materialNumber, String materialDesignation,
    String salesOffice, double standardPrice) {
        Discount expected = new Discount(salesOrg, 
        materialNumber, 
        materialDesignation,
        salesOffice,
        standardPrice);

        expected.addDiscountLevel(new DiscountLevel(1, 0.0, standardPrice, standardPrice));
        expected.addDiscountLevel(new DiscountLevel(2, 79.0, standardPrice-79, (standardPrice-79)/standardPrice));
        expected.addDiscountLevel(new DiscountLevel(3, 100.0, standardPrice-100, (standardPrice-100)/standardPrice));
        expected.addDiscountLevel(new DiscountLevel(4, 150.0, standardPrice-150, (standardPrice-150)/standardPrice));
        expected.addDiscountLevel(new DiscountLevel(5, 200.0, standardPrice-200, (standardPrice-200)/standardPrice));

        return expected;

    }
}
