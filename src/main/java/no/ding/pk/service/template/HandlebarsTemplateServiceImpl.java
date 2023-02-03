package no.ding.pk.service.template;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

@Service
public class HandlebarsTemplateServiceImpl implements HandlebarsTemplateService {

    private final Template template;

    /**
     * Creates the template engine using the Handlebars framework to produce documents with baked data.
     * @param pdfTemplateName the name of the template file without the suffix file type.
     */
    public HandlebarsTemplateServiceImpl(@Value("${pdf.template.file.name:priceOfferTemplate}") String pdfTemplateName) {
        TemplateLoader templateLoader = new ClassPathTemplateLoader("/templates", ".html");
        Handlebars handlebars = new Handlebars().registerHelper("json", Jackson2Helper.INSTANCE).with(templateLoader);

        try {
            template = handlebars.compile(pdfTemplateName);
        } catch (IOException e) {
            throw new RuntimeException(e.fillInStackTrace());
        }
    }

    @Override
    public String compileTemplate(String jsonString) {

        JsonNode jsonNode = convertStringToJsonNode(jsonString);

        Context context = getContext(jsonNode);

        try {
            return template.apply(context);
        } catch (IOException e) {
            throw new RuntimeException(e.fillInStackTrace());
        }
    }

    private JsonNode convertStringToJsonNode(String jsonString) {
        try {
            return new ObjectMapper().readValue(jsonString, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.fillInStackTrace());
        }
    }

    private Context getContext(JsonNode jsonNode) {
        return Context.newBuilder(jsonNode)
        .resolver(JsonNodeValueResolver.INSTANCE,
        JavaBeanValueResolver.INSTANCE,
        FieldValueResolver.INSTANCE,
        MapValueResolver.INSTANCE,
        MethodValueResolver.INSTANCE).build();
    }
    
}
