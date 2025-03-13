package com.ecommerce.shoppingapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class TestConfig {
    
    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .baseUrl("http://localhost:" + wireMockPort);
    }
}