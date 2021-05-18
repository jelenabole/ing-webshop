package com.ingemark.webshop.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient hnbApiClient() {
        return WebClient.create("https://api.hnb.hr");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}