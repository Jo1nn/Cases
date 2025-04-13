package com.integration.hubspot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;

import java.io.IOException;
import java.util.Map;

public class CustomOAuth2ErrorResponseErrorHandler extends OAuth2ErrorResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            super.handleError(response);
        } catch (OAuth2AuthorizationException ex) {
            // Fallback caso o HubSpot não envie `errorCode`
            Map<String, Object> body = objectMapper.readValue(response.getBody(), Map.class);
            String errorCode = (String) body.getOrDefault("error", OAuth2ErrorCodes.SERVER_ERROR);
            String description = (String) body.getOrDefault("error_description", "Erro desconhecido ao trocar o código por token.");

            OAuth2Error error = new OAuth2Error(errorCode, description, null);
            throw new OAuth2AuthorizationException(error);
        }
    }
}
