package no.ding.pk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.service.ServerVariable;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import(ObjectMapperConfig.class)
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper objectMapper;
    // Allow CORS for all requests
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jacksonMessageConverter());
        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

    private HttpMessageConverter<?> jacksonMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    @Bean
    public OpenAPI api() {
        Server server = new Server().url("https://api-internal-dev.ngn.no/price-calculator-api-dev");
//        "", "https://api-internal-dev.ngn.no/price-calculator-api-dev", "",
//                new ArrayList<ServerVariable>(), new ArrayList<>());
        return new OpenAPI()
                .info(new Info().title("Priskalkulator Service API")
                        .description("Service application for Priskalkulator")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(server))
                .externalDocs(new ExternalDocumentation()
                        .description("Priskalkulator Docs")
                        .url("https://github.com/Adact-NGN/priskalkulator-service"));

//                new Docket(DocumentationType.SWAGGER_2)
//                .select().apis(RequestHandlerSelectors.basePackage("no.ding.pk.web.controllers"))
//                .paths(PathSelectors.any()).build();
    }
}
