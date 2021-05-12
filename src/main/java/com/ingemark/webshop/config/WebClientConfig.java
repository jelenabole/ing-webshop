package com.ingemark.webshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient HNBApiClient() {
        return WebClient.create("https://api.hnb.hr/tecajn/v2");
    }

}