package no.ding.pk.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.greaterThan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.DiscountLevel;

@SpringBootTest
@TestPropertySource("/h2-db.properties")
public class DiscountServiceImplTest {
    
    @Autowired
    private DiscountService service;

    private List<Discount> testData;

    @BeforeEach
    public void setup() throws FileNotFoundException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(classLoader.getResource("discounts_simple.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), "UTF-8");

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        assertThat("Test data is empty, reading in JSON file failed.", testData, not(empty()));

    }

    @Test
    public void shouldSaveDiscountObjectWithDiscountLevels() {
        Discount expected = testData.get(0);

        Discount actual = service.save(expected);

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldBeAbleToAddDiscountLevelToDiscountWithMissingCalculatedDiscountAndPctSet() {
        Discount expected = testData.get(0);

        Discount persisted = service.save(expected);

        int initialAmountOfDiscountLevels = persisted.getDiscountLevels().size();

        DiscountLevel dl = new DiscountLevel(9, 300.0, null, null);

        persisted.addDiscountLevel(dl);

        persisted = service.save(persisted);

        assertThat(persisted.getDiscountLevels().size(), greaterThan(initialAmountOfDiscountLevels));
    }

    @Test
    public void shouldFindAllBySalesOrgZoneAndMaterialNumberWithSpecification() {
        service.saveAll(testData);

        List<Discount> actual = service.findAllBySalesOrgAndZoneAndMaterialNumber("100", "01", "113104");

        assertThat(actual.size(), greaterThan(0));

        assertThat(actual.get(0).getZone(), equalTo("01"));
    }

    @Test
    public void shouldFindAllBySalesOrgZoneAndMaterialNumberWithSpecificationWhereZoneIsNull() {
        service.saveAll(testData);

        List<Discount> actual = service.findAllBySalesOrgAndZoneAndMaterialNumber("100", null, "113104");

        assertThat(actual.size(), greaterThanOrEqualTo(2));
    }

    @Test
    public void shouldFindAllBySalesOrgZoneAndMaterialNumberWithSpecificationWhereZoneAndMaterialNumberIsNull() {
        service.saveAll(testData);

        List<Discount> actual = service.findAllBySalesOrgAndZoneAndMaterialNumber("100", null, null);

        assertThat(actual.size(), greaterThanOrEqualTo(3));
    }
}
