package no.ding.pk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Server;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("no.ding.pk.web.controllers"))
                .paths(PathSelectors.any())
                .build()
                .servers(serverInfoMetaData())
                .apiInfo(apiInfoMetaData());

    }

    private Server serverInfoMetaData() {
        return new Server(
                "Priskalkulator Service APIM server",
                "https://api-internal-dev.ngn.no/price-calculator-api-dev",
                "",
                new ArrayList<>(),
                new ArrayList<>());
    }

    private ApiInfo apiInfoMetaData() {

        return new ApiInfoBuilder().title("Priskalkulator Service API")
                .description("Service API for Priskalkulator, connecting the web application with SAP and other services.")
                .contact(new Contact("priceteam", "https://github.com/Adact-NGN/priskalkulator-service", "kjetil.torvund.minde@ngn.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")

                .build();
    }
}
