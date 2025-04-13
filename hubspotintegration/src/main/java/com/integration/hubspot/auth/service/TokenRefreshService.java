package com.integration.hubspot.auth.service;

import com.integration.hubspot.exception.AuthException;
import com.integration.hubspot.auth.dto.TokenData;
import com.integration.hubspot.config.TokenClient;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Service
public class TokenRefreshService {

    private final TokenClient tokenClient;
    private final TokenStorage tokenStorage;

    public TokenRefreshService(TokenClient tokenClient, TokenStorage tokenStorage) {
        this.tokenClient = tokenClient;
        this.tokenStorage = tokenStorage;
    }

    public void refreshToken() {
        try {
            TokenData tokenData = tokenStorage.get();
            if (tokenData == null) {
                throw new AuthException("Token não encontrado.");
            }

            // Preparando os dados para renovação do token
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.put("grant_type", Collections.singletonList("refresh_token"));
            formData.put("client_id", Collections.singletonList(tokenData.getClientId()));
            formData.put("client_secret", Collections.singletonList(tokenData.getClientSecret()));
            formData.put("refresh_token", Collections.singletonList(tokenData.getRefreshToken()));

            // Chamando o HubSpotTokenClient para trocar o refreshToken por um novo accessToken
            Map<?, ?> response = tokenClient.exchangeToken(formData);

            if (response == null || !response.containsKey("access_token")) {
                throw new AuthException("Resposta inválida da HubSpot ao renovar o token.");
            }

            // Atualizando os tokens armazenados
            String accessToken = (String) response.get("access_token");
            String refreshToken = (String) response.get("refresh_token");
            Integer expiresIn = (Integer) response.get("expires_in");

            TokenData newTokenData = new TokenData(
                    accessToken,
                    refreshToken,
                    tokenData.getClientId(),
                    tokenData.getClientSecret(),
                    Instant.now().plusSeconds(expiresIn)
            );

            tokenStorage.save(newTokenData);

        } catch (Exception ex) {
            throw new AuthException("Falha ao renovar o token de acesso", ex);
        }
    }
}
