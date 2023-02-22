package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.service.sap.StandardPriceService;
import no.ding.pk.service.sap.StandardPriceServiceImpl;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;


@Disabled
@Tag("integrationtest")
@Profile("itest")
@ActiveProfiles("itest")
@SpringBootTest
public class StandardPriceServiceImplTest {
    
    private StandardPriceService service;
    private Path workingDir;
    
    @Autowired
    private SapHttpClient sapHttpClient;
    
    @Autowired
    private SapMaterialService sapMaterialService;
    
    @Value("${cache.max.amount.items:5000}") 
    private Integer capacity;
    
    @BeforeEach
    public void setup() {
        this.workingDir = Path.of("", "src/test/resources");
        InMemory3DCache<String, String, MaterialStdPriceDTO> inMemoryCache = new PingInMemory3DCache<>(capacity);
        service = new StandardPriceServiceImpl(new ObjectMapper(), 
        inMemoryCache,
        sapMaterialService,
        sapHttpClient);
    }
    
    @Test
    void shouldGetStandardPricesBySalesOfficeSalesOrg() {
        String salesOffice = "104";
        String salesOrg = "100";
        
        List<MaterialStdPriceDTO> result = service.getStdPricesForSalesOfficeAndSalesOrg(salesOffice, salesOrg, null);
        
        assertNotNull(result);
        assertThat(result, hasSize(greaterThan(0)));
    }
    
    @Test
    void shouldBeAbleToMapStandardPrice() throws IOException {
        Path resPath = workingDir.resolve("single-standardprice.json");
        String jsonFile = Files.readString(resPath);
        ObjectMapper objectMapper = new ObjectMapper();
        MaterialStdPriceDTO stdPriceDTO = objectMapper.readValue(jsonFile, MaterialStdPriceDTO.class);
        
        assertThat(stdPriceDTO.getMaterial(), Is.is("50101"));
    }
}
