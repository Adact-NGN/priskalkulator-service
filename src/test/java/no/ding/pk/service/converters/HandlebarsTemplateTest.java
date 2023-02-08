package no.ding.pk.service.converters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.greaterThan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.gson.Gson;

import io.swagger.v3.oas.models.headers.Header;
import no.ding.pk.config.SchedulingTestConfig;
import no.ding.pk.domain.User;
import no.ding.pk.service.template.HandlebarsTemplateService;
import no.ding.pk.service.template.HandlebarsTemplateServiceImpl;

import no.ding.pk.web.dto.sap.CustomerDTO;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandlebarsTemplateTest {

    private final HandlebarsTemplateService service = new HandlebarsTemplateServiceImpl("priceOfferTemplate");

    @Test
    public void whenThereIsNoTemplateFile_ThenCompilesInline() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline("Hi {{this}}");

        String templateString = template.apply("PING");

        assertThat(templateString, equalTo("Hi PING"));
    }

    @Test
    public void whenParameterObjectIsSupplied_ThenDisplay() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline("Hi {{name}}");

        User user = User.builder().name("PING").build();

        String templateString = template.apply(user);

        assertThat(templateString, equalTo("Hi PING"));
    }

    @Test
    public void shouldInsertCustomerDataToTable() throws IOException {
        TemplateLoader priceOfferTemplateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars().with(priceOfferTemplateLoader);
        Template template = handlebars.compileInline("Hi {{name1}} with customer number {{customerNumber}}");

        String customerName = "Europris Telem Notodden";
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerNumber("132456")
                .name1(customerName)
                .build();

        String temaplteString = template.apply(customerDTO);


        assertThat(temaplteString, notNullValue());
        assertThat(temaplteString, containsString(customerName));
    }

    @Test
    public void shouldUseJsonToPopulateTemplate() throws IOException {
        String jsonString = getPriceOfferJson("templateData_v2.json");

        assertThat(jsonString.length(), greaterThan(0));

        JsonNode json = new ObjectMapper().readValue(jsonString, JsonNode.class);

        TemplateLoader priceOfferTemplateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars().registerHelper("json", Jackson2Helper.INSTANCE).with(priceOfferTemplateLoader);

        Template template = handlebars.compile("priceOfferTemplate");

        Context context = Context
        .newBuilder(json)
        .resolver(JsonNodeValueResolver.INSTANCE,
        JavaBeanValueResolver.INSTANCE,
        FieldValueResolver.INSTANCE,
        MapValueResolver.INSTANCE,
        MethodValueResolver.INSTANCE).build();

        String result = template.apply(context);

        assertThat(result, notNullValue());
        assertThat(result, containsString("Europris Telem Notodden"));
        assertThat(result, containsString("Wolfgang@farris-bad.no"));
        assertThat(result, containsString("Sarpsborg/Fredrikstad"));

        writeToFile(result, "handlebarsOut.html");
    }

    @Test
    public void shouldUserTemplateServiceToCompileTemplate() {
        String jsonString = getPriceOfferJson("templateData_v2.json");

        String result = service.compileTemplate(jsonString);

        assertThat(result, notNullValue());
        assertThat(result, containsString("Europris Telem Notodden"));
        assertThat(result, containsString("Wolfgang@farris-bad.no"));
        assertThat(result, containsString("Sarpsborg/Fredrikstad"));
    }

    private void writeToFile(String data, String fileName) {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePathOut = resourceDirectory.toFile().getAbsolutePath() + "/" + fileName;

        File outputFile = new File(absolutePathOut);

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            byte[] fileBytes = data.getBytes();

            outputStream.write(fileBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPriceOfferJson(String dataFileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("templates/" + dataFileName).getFile());

        assertThat(file.exists(), is(true));

        Path filePath = Path.of(file.getAbsolutePath());
        
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e.fillInStackTrace());
        }
    }
}
