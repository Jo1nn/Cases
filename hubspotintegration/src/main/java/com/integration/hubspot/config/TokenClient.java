package com.integration.hubspot.config;

import com.integration.hubspot.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenClient {

    @Qualifier("hubspotWebClient")
    private WebClient webClient;

    public Map<?, ?> exchangeToken(MultiValueMap<String, String> formData) {
        try {
            return executeTokenRequest(formData);
        } catch (Exception e) {
            throw new AuthException("Erro ao comunicar com a API da HubSpot para troca de token.", e);
        }
    }

    // Renovação do access token utilizando o refresh token
    public Map<?, ?> refreshToken(String refreshToken, String clientId, String clientSecret) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("refresh_token", refreshToken);

            return executeTokenRequest(formData);
        } catch (Exception e) {
            throw new AuthException("Erro ao comunicar com a API da HubSpot para renovar o token.", e);
        }
    }

    private Map<?, ?>  executeTokenRequest(MultiValueMap<String, String> formData) {
        return webClient.post()
                .uri("/oauth/v1/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}