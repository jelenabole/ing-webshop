package com.ingemark.webshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("test")
public class WebClientTestConfig {

    @Bean
    @Primary
    public WebClient hnbApiClient() {
        return WebClient.create("http://localhost:9090");
    }

}