package no.ding.pk;

import com.azure.security.keyvault.secrets.SecretClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import no.ding.pk.domain.SalesRole;
import no.ding.pk.service.SalesRoleService;
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
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@EnableSwagger2
@SpringBootApplication(scanBasePackages = "no.ding.pk.*")

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

    private final SecretClient secretClient;

    public App(SecretClient secretClient) {
        this.secretClient = secretClient;

        log.debug(secretClient.getSecret("pk-datasource-username").getValue());
    }

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
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
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

//    @Bean
//    public CommandLineRunner cmdLineRunner(DiscountService discountService, SalesRoleService salesRoleService, ObjectMapper objectMapper) {
//        return args -> {
//            initializeDiscounts(discountService, objectMapper);
//
//            initializeSalesRoles(salesRoleService, objectMapper);
//        };
//    }



    private static void initializeSalesRoles(SalesRoleService salesRoleService, ObjectMapper objectMapper) {
        TypeReference<List<SalesRole>> salesRoleTypeRef = new TypeReference<>() {
        };

        List<String> existingSalesRolesNames = salesRoleService.getAllSalesRoles().stream().map(SalesRole::getRoleName).collect(Collectors.toList());

        InputStream salesRoleInputStream = TypeReference.class.getResourceAsStream("/sales_roles.json");

        try {
            List<SalesRole> salesRoles = objectMapper.readValue(salesRoleInputStream, salesRoleTypeRef);

            List<SalesRole> toPersist = salesRoles.stream().filter(salesRole -> !existingSalesRolesNames.contains(salesRole.getRoleName())).collect(Collectors.toList());
            log.debug("Sales roles to persist: {}", toPersist.size());

            salesRoleService.saveAll(toPersist);
            log.debug("Sales roles saved");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
