package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.hamcrest.core.Is;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.web.dto.MaterialDTO;

public class StandardPriceServiceImplTest {

    private StandardPriceService service;
    private Path workingDir;

    @BeforeEach
    public void setup() {
        this.workingDir = Path.of("", "src/test/resources");
        service = new StandardPriceServiceImpl("AZURE_ECOM", "AzureEcom@NGN2022", new ObjectMapper(), mock(InMemoryCache.class));
    }

    @Ignore
    @Test
    void shouldGetStandardPricesBySalesOfficeSalesOrg() {
        String salesOffice = "104";
        String salesOrg = "100";
        
        List<MaterialDTO> result = service.getStdPricesForSalesOfficeAndSalesOrg(salesOffice, salesOrg);

        assertNotNull(result);
        assertThat(result, hasSize(greaterThan(0)));

        JSONObject json = new JSONObject(result);
    }

    @Test
    void shouldBeAbleToMapStandardPrice() throws StreamReadException, DatabindException, IOException, URISyntaxException {
        Path resPath = workingDir.resolve("single-standardprice.json");
        String jsonFile = Files.readString(resPath);
        ObjectMapper objectMapper = new ObjectMapper();
        MaterialDTO stdPriceDTO = objectMapper.readValue(jsonFile, MaterialDTO.class);

        assertThat(stdPriceDTO.getMaterial(), Is.is("50101"));
    }
}
