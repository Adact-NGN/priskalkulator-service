package no.ding.pk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@ContextConfiguration(classes = {WebTestConfig.class})
@WebAppConfiguration
@SpringBootTest(properties = {
        "sap.username=sapUser",
        "sap.password=sapPassword",
        "sap.api.customer.url=SAP_API_CUSTOMER_URL",
        "sap.api.contact.person.url=SAP_API_CONTACT_PERSON_URL",
        "sap.api.standard.price.url=SAP_API_STANDARD_PRICE_URL",
        "sap.api.salesorg.url=SAP_API_SALESORG_URL",
        "sap.api.material.url=SAP_API_MATERIAL_URL",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",

        "spring.jpa.open-in-view=false",

        "logging.level.no.ding.pk=DEBUG",
        "logging.level.org.springframework.web=DEBUG",
        "logging.level.com.microsoft.aad.msal4j=DEBUG",
        "pdf.template.file.name=pdfTemplateName",
        "spring.cloud.azure.active-directory.app-id-uri=AD_APP_ID",
        "CLIENT_ID=I_TEST_CLIENT_ID",
        "AUTHORITY=I_TEST_AUTHORITY",
        "SECRET=AD_SECRET",
        "SCOPE=AD_SCOPE",
        "AD_USER_INFO_SELECT_LIST=AD_USER_INFO_LIST"
}, classes = {
        AzureConfig.class,
//        SpringFoxConfig.class
})
public class GenerateSwaggerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void generateSwagger() throws Exception {
        mockMvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andDo((result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object jsonObject = objectMapper.readValue(contentAsString, Object.class);
                    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                    FileUtils.writeStringToFile(new File("spec/swagger.json"), prettyJson, StandardCharsets.UTF_8);
                })).andExpect(status().isOk());
    }
}
