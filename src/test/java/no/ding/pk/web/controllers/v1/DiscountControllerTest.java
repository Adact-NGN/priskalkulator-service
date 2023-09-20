package no.ding.pk.web.controllers.v1;

import no.ding.pk.domain.Discount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;

@Disabled("Move all test scenarios to service")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/h2-db.properties")
@SqlConfig(commentPrefix = "#")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(value = {"/discout_db_scripts/drop_schema.sql", "/discout_db_scripts/create_schema.sql"})
@Sql(value = {"/discout_db_scripts/discount_matrix.sql", "/discout_db_scripts/discount_levels.sql"})
public class DiscountControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String salesOffice = "100";

    @Test
    public void shouldReturnListOfDiscountsForMaterialsWithNoZoneDifferentiatedPrices() {
        String materials = "50106,50107";
        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers=" + materials, Discount[].class);

        assertThat(responseEntity.getBody(), arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldReturnEmptyListWhenTheresIsNoMaterialsForSalesOffice() {
        String materials = "50106,50107";
        String salesOffice = "104";
        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers=" + materials, Discount[].class);

        assertThat(responseEntity.getBody(), arrayWithSize(0));
    }

    @Test
    public void shouldReturnListOfDiscountsForMaterialsWithEmptyZoneRequestParameter() {
        String materials = "50106,50107";

        Map<String, String> params = new HashMap<>();
        params.put("materialNumbers", materials);
        params.put("serverPort", String.valueOf(serverPort));
        params.put("salesOffice", salesOffice);

        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:{serverPort}/api/v1/discount/in-list/100?salesOffice={salesOffice}&materialNumbers={materialNumbers}&zones=", Discount[].class, params);

        assertThat(responseEntity.getBody(), arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldNotReturnDiscountsForMaterialsWithZoneDifferentiatedPrices() {
        String materials = "50101,50102";

        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers=" + materials, Discount[].class);

        assertThat(responseEntity.getBody(), arrayWithSize(0));
    }

    @Test
    public void shouldNotReturnDiscountsForSingleMaterialWithZoneDifferentiatedPrices() {
        String materials = "50101";

        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers=" + materials, Discount[].class);

        assertThat(responseEntity.getBody(), arrayWithSize(0));
    }

    @Test
    public void shouldReturnDiscountsForSingleMaterialWithZoneDifferentiatedPrices() {
        String materials = "50101";
        String zones = "1,2";

        Map<String, String> params = new HashMap<>();
        params.put("materialNumbers", materials);
        params.put("zones", zones);

        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers={materialNumbers}&zones={zones}",
                Discount[].class,
                params);

        assertThat(responseEntity.getBody(), arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldReturnDiscountsForSingleMaterialWithSingleZoneDifferentiatedPrices() {
        String materials = "50101";
        String zones = "1";

        Map<String, String> params = new HashMap<>();
        params.put("salesOffice", salesOffice);
        params.put("materialNumbers", materials);
        params.put("zones", zones);

        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice={salesOffice}&materialNumbers={materialNumbers}&zones={zones}",
                Discount[].class,
                params);

        assertThat(responseEntity.getBody(), arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldReturnDiscountsForMaterialsWithZoneDifferentiatedPrices() {
        String materials = "50101,50102";
        String zones = "1,2";

        Map<String, String> params = new HashMap<>();
        params.put("materialNumbers", materials);
        params.put("zones", zones);

        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers={materialNumbers}&zones={zones}",
                Discount[].class,
                params);

        assertThat(responseEntity.getBody(), arrayWithSize(greaterThan(0)));
    }

    @Test
    public void shouldGetResultForSingleMaterialWithNoZoneDifferentiatedPrices() {
        String materials = "50106";
        ResponseEntity<Discount[]> responseEntity = this.restTemplate.getForEntity("http://localhost:" + serverPort + "/api/v1/discount/in-list/100?salesOffice=" + salesOffice + "&materialNumbers=" + materials, Discount[].class);

        assertThat(responseEntity.getBody(), arrayWithSize(greaterThan(0)));
    }
}
