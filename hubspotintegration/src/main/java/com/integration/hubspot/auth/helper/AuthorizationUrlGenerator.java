package com.integration.hubspot.auth.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationUrlGenerator {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    public String generateAuthorizationUrl() {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("hubspot");

        String clientId = registration.getClientId();
        String redirectUri = registration.getRedirectUri();
        String scope = String.join(" ", registration.getScopes());

        return String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
                registration.getProviderDetails().getAuthorizationUri(),
                clientId,
                redirectUri,
                scope
        );
    }

    // Sobrecarga opcional para permitir escopos personalizados
    public String generateAuthorizationUrl(String customScope) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("hubspot");

        return String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
                registration.getProviderDetails().getAuthorizationUri(),
                registration.getClientId(),
                registration.getRedirectUri(),
                customScope
        );
    }
}
