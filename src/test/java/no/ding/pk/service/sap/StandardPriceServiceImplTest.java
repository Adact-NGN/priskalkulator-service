package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.utils.SapHttpClient;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.Is;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@DataJpaTest
@TestPropertySource("classpath:h2-db.properties")
public class StandardPriceServiceImplTest {
    
    private StandardPriceService service;
    private Path workingDir;
    
    private SapHttpClient sapHttpClient;
    
    private SapMaterialService sapMaterialService;

    private ModelMapper modelMapper;

    @MockBean
    private SalesOrgService salesOrgService;

    @Autowired
    private SalesRoleRepository salesRoleRepository;

    @Value("${cache.max.amount.items:5000}")
    private Integer capacity;
    private InMemory3DCache<String, String, MaterialStdPriceDTO> inMemoryCache;

    @BeforeEach
    public void setup() throws IOException {

        sapHttpClient = mock(SapHttpClient.class);
        sapMaterialService = mock(SapMaterialService.class);

        MaterialService materialService = mock(MaterialService.class);

        modelMapper = new ModelMapperV2Config().modelMapperV2(materialService, salesRoleRepository);

        this.workingDir = Path.of("", "src/test/resources");

        ClassLoader classLoader = getClass().getClassLoader();
        // OBS! Remember to package the project for the test to find the resource file in the test-classes directory.
        mockCallForStandardPrice(classLoader);

        mockMaterialServiceResponse(classLoader);

        inMemoryCache = new PingInMemory3DCache<>(capacity);
        service = new StandardPriceServiceImpl("http://saptest.norskgjenvinning.no", new ObjectMapper(),
        sapMaterialService,
        sapHttpClient, modelMapper, salesOrgService);
    }

    private void mockMaterialServiceResponse(ClassLoader classLoader) throws IOException {
        File file = new File(classLoader.getResource("materials100.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        JSONObject jsonObjectResult = new JSONObject(json);

        JSONArray result = jsonObjectResult.getJSONArray("value");

        ObjectMapper om = new ObjectMapper();

        List<MaterialDTO> materialDTOS = new ArrayList<>();

        for(int i = 0; i < result.length(); i++) {
            JSONObject jsonObject = result.getJSONObject(i);

            MaterialDTO materialDTO = om.readValue(jsonObject.toString(), MaterialDTO.class);

            materialDTOS.add(materialDTO);
        }

        doReturn(materialDTOS).when(sapMaterialService).getAllMaterialsForSalesOrgBy(anyString(), anyInt(), anyInt());
    }

    private void mockCallForStandardPrice(ClassLoader classLoader) throws IOException {
        File file = new File(classLoader.getResource("standardPrices100104.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        doReturn(HttpRequest.newBuilder().uri(URI.create("https://test")).build()).when(sapHttpClient).createGetRequest(anyString(), any());

        HttpResponse<String> stdPriceResponse = new HttpResponse<>() {
            @Override
            public int statusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return json;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };

        doReturn(stdPriceResponse).when(sapHttpClient).getResponse(any());
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
    void shouldGetStandardPricesBySalesOfficeSalesOrgAndZone() {
        String salesOffice = "104";
        String salesOrg = "100";

        Map<String, MaterialPrice> result = service.getStandardPriceForSalesOrgAndSalesOfficeMap(salesOffice, salesOrg, "0000000002");

        assertNotNull(result);
        assertThat(result.keySet(), hasSize(greaterThan(0)));
    }
    
    @Test
    void shouldBeAbleToMapStandardPrice() throws IOException {
        Path resPath = workingDir.resolve("single-standardprice.json");
        String jsonFile = Files.readString(resPath);
        ObjectMapper objectMapper = new ObjectMapper();
        MaterialStdPriceDTO stdPriceDTO = objectMapper.readValue(jsonFile, MaterialStdPriceDTO.class);
        
        assertThat(stdPriceDTO.getMaterial(), Is.is("50101"));
    }

    @Disabled
    @Test
    void shouldGetMaterialStandardPriceFromCache() {
        String salesOrg = "100";
        String salesOffice = "104";

        List<MaterialStdPriceDTO> stdPriceDTOS = service.getStandardPriceForSalesOrgSalesOfficeAndMaterial(salesOrg, salesOffice, "50101", "01");

        assertThat(stdPriceDTOS, hasSize(1));

        stdPriceDTOS = service.getStandardPriceForSalesOrgSalesOfficeAndMaterial(salesOrg, salesOffice, "50101", "01");

        assertThat(stdPriceDTOS, hasSize(1));
        assertThat(inMemoryCache.size("100"), is(1));
    }

    @Test
    void shouldBeAbleToSearchForMaterialsInCache() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("standardPrices100104.json").getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.reader();

        JsonNode jsonNode = objectReader.readTree(json);

        jsonNode.get("d").get("results").forEach(node -> {
            MaterialStdPriceDTO materialStdPriceDTO = null;
            try {
                materialStdPriceDTO = objectReader.readValue(node, MaterialStdPriceDTO.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String combination = createSalesOfficeMaterialNumberCombination(materialStdPriceDTO.getSalesOffice(), materialStdPriceDTO.getMaterial(), materialStdPriceDTO.getZone(), materialStdPriceDTO.getDeviceType());

            inMemoryCache.put("100", combination, materialStdPriceDTO);
        });

        assertThat(inMemoryCache.size("100"), greaterThan(0));

        List<MaterialStdPriceDTO> materialStdPriceDTOS = inMemoryCache.searchFor("100", "50101");

        assertThat(materialStdPriceDTOS, hasSize(greaterThan(0)));
    }

    private String createSalesOfficeMaterialNumberCombination(String salesOffice, String material, String zone, CharSequence deviceType) {
        StringBuilder sb = new StringBuilder();
        sb.append(salesOffice).append("_").append(material);

        if(StringUtils.isNotBlank(zone)) {
            sb.append("_").append(zone);
        }

        if(StringUtils.isNotBlank(deviceType)) {
            sb.append("_").append(deviceType);
        }

        return sb.toString();
    }
}
