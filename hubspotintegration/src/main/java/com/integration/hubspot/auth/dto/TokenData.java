package com.integration.hubspot.auth.dto;

import java.time.Instant;

public class TokenData {

    private String accessToken;

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private String refreshToken;
    private String clientId;
    private String clientSecret;
    private Instant expiration;

    // Construtor com todos os parâmetros
    public TokenData(String accessToken, String refreshToken, String clientId, String clientSecret, Instant expiration) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.expiration = expiration;
    }

    // Remova qualquer outro construtor desnecessário
}
