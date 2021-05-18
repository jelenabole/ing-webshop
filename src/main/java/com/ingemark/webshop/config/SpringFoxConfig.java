package com.ingemark.webshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.Collections;

@Configuration
public class SpringFoxConfig {

    @Bean
    Docket swaggerConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ingemark.webshop"))
                .build()
                .apiInfo(apiDetails());
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .docExpansion(DocExpansion.LIST)
                .build();
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "Webshop API documentation",
                "API documentation for ing-webshop application",
                "1.0",
                "Terms of service",
                new Contact("Jelena", "", "jelenabole@gmail.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                Collections.emptyList()
        );
    }
}