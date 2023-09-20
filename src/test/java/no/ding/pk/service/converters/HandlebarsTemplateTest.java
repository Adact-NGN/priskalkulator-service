package no.ding.pk.service.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import no.ding.pk.domain.User;
import no.ding.pk.service.template.HandlebarsTemplateService;
import no.ding.pk.service.template.HandlebarsTemplateServiceImpl;
import no.ding.pk.web.dto.sap.CustomerDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Disabled
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
