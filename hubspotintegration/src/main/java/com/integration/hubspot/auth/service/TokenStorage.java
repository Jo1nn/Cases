package com.integration.hubspot.auth.service;

import com.integration.hubspot.util.CryptoUtils;
import com.integration.hubspot.auth.dto.TokenData;
import com.integration.hubspot.exception.AuthException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenStorage {

    private String encryptedAccessToken;
    private String encryptedRefreshToken;
    private String encryptedClientId;
    private String encryptedClientSecret;
    private Instant expiration;

    // Método para salvar o TokenData de forma segura (com criptografia)
    public void save(TokenData tokenData) throws Exception {
        System.out.println("Access token original: " + tokenData.getAccessToken());

        this.encryptedAccessToken = CryptoUtils.encrypt(tokenData.getAccessToken());
        this.encryptedRefreshToken = CryptoUtils.encrypt(tokenData.getRefreshToken());
        this.encryptedClientId = CryptoUtils.encrypt(tokenData.getClientId());
        this.encryptedClientSecret = CryptoUtils.encrypt(tokenData.getClientSecret());
        this.expiration = tokenData.getExpiration();
    }

    // Método para recuperar o TokenData de forma segura (descriptografado)
    public TokenData get() {
        try {
            String accessToken = CryptoUtils.decrypt(encryptedAccessToken);
            String refreshToken = CryptoUtils.decrypt(encryptedRefreshToken);
            String clientId = CryptoUtils.decrypt(encryptedClientId);
            String clientSecret = CryptoUtils.decrypt(encryptedClientSecret);

            System.out.println("Access token original: " + encryptedAccessToken);
            System.out.println("Access token descriptografado: " + accessToken);

            return new TokenData(accessToken, refreshToken, clientId, clientSecret, expiration);
        } catch (Exception e) {
            System.err.println("Erro ao descriptografar os tokens: " + e.getMessage());
            throw new AuthException("Falha ao recuperar o token. Verifique a integridade dos dados criptografados.", e);
        }
    }

    // Método para verificar se o token está expirado
    public boolean isExpired() {
        return expiration == null || expiration.isBefore(Instant.now());
    }
}
