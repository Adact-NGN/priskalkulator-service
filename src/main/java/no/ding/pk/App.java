package no.ding.pk;

import java.net.MalformedURLException;
import java.util.List;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;

import no.ding.pk.domain.User;
import no.ding.pk.web.dto.AdUserDTO;

@EnableScheduling
@EnableSwagger2
@SpringBootApplication

@PropertySource({
    "classpath:application.properties", 
    "classpath:sap.properties", 
    "classpath:msal.properties"
})
public class App implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
    
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
        
        ObjectMapper objectMapper = objectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }
    
    @Value("${CLIENT_ID}")
    private String clientId;
    @Value("${AUTHORITY}")
    private String authority;
    @Value("${SECRET}")
    private String secret;
    @Value("${SCOPE}")
    private String scope;
    
    @Bean
    public ConfidentialClientApplication confidentialClientApplication() throws MalformedURLException {
        log.debug("Building ConfidentialClientApplication with client id: " + clientId);
        return ConfidentialClientApplication.builder(clientId, 
        ClientCredentialFactory.createFromSecret(secret))
        .authority(authority)
        .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Hibernate5Module());

        return objectMapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(AdUserDTO.class, User.class)
        .addMapping(AdUserDTO::getAdId, User::setAdId)
        .addMapping(AdUserDTO::getSureName, User::setSureName);
        
        return modelMapper;
    }

    @Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

    @Profile("!test")
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
        .select().apis(RequestHandlerSelectors.basePackage("no.ding.pk.web.controllers"))
        .paths(PathSelectors.any()).build();
    }
}
