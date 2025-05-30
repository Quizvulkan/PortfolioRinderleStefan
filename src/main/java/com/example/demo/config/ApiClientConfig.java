package com.example.demo.config;

import org.openapitools.client.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiClientConfig {

    @Bean
    public ApiClient mazeApiClient() {
        ApiClient client = new ApiClient();
        client.setBasePath("https://mazegame.rinderle.info");
        return client;
    }
}