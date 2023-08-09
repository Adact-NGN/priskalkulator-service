package no.ding.pk.service.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.Discount;
import no.ding.pk.domain.offer.Material;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.Zone;
import no.ding.pk.service.DiscountService;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SpringBootTest
@Transactional
@TestPropertySource("/h2-db.properties")
class ZoneServiceImplTest {

    @Autowired
    private ZoneService service;

    @Autowired
    private DiscountService discountService;

    private List<Discount> testData;

    @BeforeEach
    public void setup() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        File file = new File(Objects.requireNonNull(classLoader.getResource("discounts.json")).getFile());

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
    public void shouldSaveAllZones() {
        addMissingDiscounts();
        Optional<Discount> optionalDiscount = testData.stream().filter(p -> p.getMaterialNumber().equals("B-0120")).findAny();

        if(optionalDiscount.isEmpty()) {
            throw new RuntimeException("Discount not found.");
        }

        Material material = Material.builder()
                .designation("Avfall - Utstyr")
                .materialNumber("B-0120")
                .build();
        PriceRow priceRow = PriceRow.builder()
                .material(material)
                .build();
        List<Zone> zoneList = List.of(Zone.builder()
                .zoneId("0000000001")
                .isStandardZone(true)
                .postalCode("1001")
                .postalName("Oslo")
                .priceRows(List.of(priceRow))
                .build()
        );

        List<Zone> actual = service.saveAll(zoneList, "100", "100");

        assertThat(actual, hasSize(greaterThan(0)));
    }

    protected void addMissingDiscounts() {
        List<String> toPersist = discountService.findAll().stream().map(Discount::getMaterialNumber).toList();

        List<Discount> discounts = testData.stream().filter(discount -> !toPersist.contains(discount.getMaterialNumber())).collect(Collectors.toList());

        if(discounts.size() > 0) {
            discountService.saveAll(discounts);
        }
    }

}