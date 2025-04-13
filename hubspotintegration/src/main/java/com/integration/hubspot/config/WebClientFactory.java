package com.integration.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientFactory {

    // Configuração centralizada do WebClient para a API da HubSpot
    @Bean
    public WebClient hubSpotWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.hubapi.com")  // Base URL da API da HubSpot
                .defaultHeader("Accept", "application/json")  // Cabeçalho comum para todas as requisições
                .build();
    }
}
