package no.ding.pk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;
import no.ding.pk.listener.CleanUpH2DatabaseListener;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@Disabled("Need to isolate the scope")
@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanUpH2DatabaseListener.class})
@TestPropertySource("/h2-db.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DiscountServiceImplTest {

    @Autowired
    private DiscountService service;

    private List<Discount> testData;

    @BeforeEach
    public void setup() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(Objects.requireNonNull(classLoader.getResource("discounts_simple.json")).getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        assertThat("JSON is empty", json, not(emptyOrNullString()));

        testData = new ArrayList<>();

        JSONArray results = new JSONArray(json);

        ObjectMapper om = new ObjectMapper();

        for(int i = 0; i < results.length(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);

            try {
                Discount discount = om.readValue(jsonObject.toString(), Discount.class);
                testData.add(discount);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        assertThat("Test data is empty, reading in JSON file failed.", testData, not(empty()));
    }

    @Test
    public void shouldSaveDiscountObjectWithDiscountLevels() {
        Discount expected = Discount.builder()
                .salesOrg("100")
                .salesOffice("100")
                .materialNumber("Test")
                .materialDesignation("Test")
                .standardPrice(1234.0)
                .build();

        Discount actual = service.save(expected);

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldBeAbleToAddDiscountLevelToDiscountWithMissingCalculatedDiscountAndPctSet() {
        Discount expected = Discount.builder()
                .salesOrg("100")
                .salesOffice("100")
                .materialNumber("Test2")
                .materialDesignation("Test2")
                .standardPrice(1234.0)
                .build();

        expected.setDiscountLevels(new ArrayList<>());

        Discount persisted = service.save(expected);

        int initialAmountOfDiscountLevels = persisted.getDiscountLevels().size();

        DiscountLevel dl = new DiscountLevel(9, 300.0, null, null);

        persisted.addDiscountLevel(dl);

        persisted = service.save(persisted);

        assertThat(persisted.getDiscountLevels().size(), greaterThan(initialAmountOfDiscountLevels));
    }

    @Test
    public void shouldFindAllBySalesOrgZoneAndMaterialNumberWithSpecification() {
        addMissingDiscounts();

        List<Discount> actual = service.findAllBySalesOrgAndZoneAndMaterialNumber("100", "01", "113104");

        assertThat(actual.size(), greaterThan(0));

        assertThat(actual.get(0).getDiscountLevels().get(0).getZone(), equalTo("01"));
    }

    @Test
    public void shouldFindAllBySalesOrgZoneAndMaterialNumberWithSpecificationWhereZoneIsNull() {
        addMissingDiscounts();

        List<Discount> actual = service.findAllBySalesOrgAndZoneAndMaterialNumber("100", null, "C-05L-L");

        assertThat(actual.size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void shouldFindAllBySalesOrgZoneAndMaterialNumberWithSpecificationWhereZoneAndMaterialNumberIsNull() {
        addMissingDiscounts();

        List<Discount> actual = service.findAllBySalesOrgAndZoneAndMaterialNumber("100", null, null);

        assertThat(actual.size(), greaterThanOrEqualTo(3));
    }

    @Test
    public void shouldFindDiscountBySalesOrgAndMaterialNumber() {
        addMissingDiscounts();

        List<String> materialNumbers = List.of("113103");
        List<DiscountLevel> actual = service.findAllDiscountLevelsForDiscountBySalesOrgAndSalesOfficeAndMaterialNumber("100", "100", materialNumbers.get(0), null);

        assertThat(actual.size(), greaterThanOrEqualTo(5));
    }

    @Test
    public void shouldFindAllDiscountsInListForSalesOrgAndSalesOffice() {
        addMissingDiscounts();

        List<String> materialNumbers = List.of("113103");
        List<Discount> actual = service.findAllDiscountForDiscountBySalesOrgAndSalesOfficeAndMaterialNumberIn("100", "100", materialNumbers);

        assertThat(actual, hasSize(1));

    }

    protected void addMissingDiscounts() {
        List<String> toPersist = service.findAll().stream().map(Discount::getMaterialNumber).toList();

        List<Discount> discounts = testData.stream().filter(discount -> !toPersist.contains(discount.getMaterialNumber())).collect(Collectors.toList());

        if(discounts.size() > 0) {
            service.saveAll(discounts);
        }
    }
}
