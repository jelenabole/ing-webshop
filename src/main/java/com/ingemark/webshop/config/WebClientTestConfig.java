package com.ingemark.webshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("integration-test")
public class WebClientTestConfig {

    @Bean
    WebClient hnbApiClient() {
        return WebClient.create("http://localhost:9090");
    }

}