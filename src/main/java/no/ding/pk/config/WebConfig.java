package no.ding.pk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

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
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("no.ding.pk.web.controllers"))
                .paths(PathSelectors.any()).build();
    }
}
