package com.integration.hubspot.auth.service;

import com.integration.hubspot.auth.helper.AuthorizationUrlGenerator;
import com.integration.hubspot.auth.dto.TokenData;
import com.integration.hubspot.exception.AuthException;
import com.integration.hubspot.config.StateStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenStorage tokenStorage;
    private final StateStorage stateStorage;
    private final TokenRefreshService tokenRefreshService;
    private final AuthorizationUrlGenerator authorizationUrlGenerator;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // usada para chamada manual ao token endpoint

    public void exchangeCodeForToken(String authorizationCode, String state) {
        try {
            if (!stateStorage.isValid(state)) {
                throw new AuthException("State inválido ou expirado.");
            }

            ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("hubspot");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("redirect_uri", registration.getRedirectUri());
            formData.add("code", authorizationCode);
            formData.add("client_id", registration.getClientId());
            formData.add("client_secret", registration.getClientSecret());

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    registration.getProviderDetails().getTokenUri(),
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<?, ?> body = response.getBody();

            if (body == null || !body.containsKey("access_token")) {
                throw new AuthException("Resposta inválida da HubSpot ao trocar o código por token.");
            }

            String accessToken = (String) body.get("access_token");
            String refreshToken = (String) body.get("refresh_token");
            Integer expiresIn = (Integer) body.get("expires_in");

            TokenData tokenData = new TokenData(
                    accessToken,
                    refreshToken,
                    registration.getClientId(),
                    registration.getClientSecret(),
                    Instant.now().plusSeconds(expiresIn)
            );

            System.out.println(tokenData);

            tokenStorage.save(tokenData);
            stateStorage.remove(state);

        } catch (HttpClientErrorException e) {
            throw new AuthException("Erro ao trocar código por token: " + e.getResponseBodyAsString(), e);
        } catch (Exception ex) {
            throw new AuthException("Falha ao trocar o código por token de acesso", ex);
        }
    }

    /*public String generateAuthorizationUrl() {
        return authorizationUrlGenerator.generateAuthorizationUrl();
    }*/

    public String generateAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        stateStorage.save(state); // você precisa implementar isso

        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("hubspot");

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(registration.getProviderDetails().getAuthorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", registration.getClientId())
                .queryParam("redirect_uri", registration.getRedirectUri())
                .queryParam("scope", String.join(" ", registration.getScopes()))
                .queryParam("state", state); // agora o parâmetro está aqui

        return uriBuilder.toUriString();
    }

    public String getRefreshToken() {
        TokenData tokenData = tokenStorage.get();
        if (tokenData == null || tokenData.getRefreshToken() == null) {
            throw new AuthException("Token de atualização não disponível.");
        }
        return tokenData.getRefreshToken();
    }

    public void refreshAccessToken() {
        try {
            TokenData tokenData = tokenStorage.get();
            if (tokenData == null) {
                throw new AuthException("Token não encontrado.");
            }

            tokenRefreshService.refreshToken();

        } catch (Exception ex) {
            throw new AuthException("Falha ao renovar o token de acesso", ex);
        }
    }
}
