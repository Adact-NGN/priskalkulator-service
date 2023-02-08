package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import no.ding.pk.web.dto.web.client.MaterialDTO;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Disabled
@Tag("integrationtest")
@Profile("itest")
@ActiveProfiles("itest")
public class StandardPriceServiceImplTest {

    private StandardPriceService service;
    private Path workingDir;

    @BeforeEach
    public void setup() {
        this.workingDir = Path.of("", "src/test/resources");
        InMemoryCache<String, String, MaterialStdPriceDTO> inMemoryCache = new MaterialInMemoryCache<>();
        service = new StandardPriceServiceImpl("AZURE_ECOM", "AzureEcom@NGN2022", new ObjectMapper(), inMemoryCache);//mock(InMemoryCache.class));
    }

    @Test
    void shouldGetStandardPricesBySalesOfficeSalesOrg() {
        String salesOffice = "104";
        String salesOrg = "100";
        
        List<MaterialStdPriceDTO> result = service.getStdPricesForSalesOfficeAndSalesOrg(salesOffice, salesOrg);

        assertNotNull(result);
        assertThat(result, hasSize(greaterThan(0)));
    }

    @Test
    void shouldBeAbleToMapStandardPrice() throws StreamReadException, DatabindException, IOException, URISyntaxException {
        Path resPath = workingDir.resolve("single-standardprice.json");
        String jsonFile = Files.readString(resPath);
        ObjectMapper objectMapper = new ObjectMapper();
        MaterialStdPriceDTO stdPriceDTO = objectMapper.readValue(jsonFile, MaterialStdPriceDTO.class);

        assertThat(stdPriceDTO.getMaterial(), Is.is("50101"));
    }
}
