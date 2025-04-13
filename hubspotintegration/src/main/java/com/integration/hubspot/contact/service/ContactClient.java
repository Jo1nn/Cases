package com.integration.hubspot.contact.service;

import com.integration.hubspot.auth.service.TokenStorage;
import com.integration.hubspot.contact.dto.ContactCreateRequest;
import com.integration.hubspot.contact.dto.ContactListResponse;
import com.integration.hubspot.contact.dto.ContactResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactClient {

    private final WebClient hubSpotWebClient;
    private final TokenStorage tokenStorage;

    public Optional<ContactResponse> fetchById(Long id) {
        try {
            return hubSpotWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/crm/v3/objects/contacts/{id}")
                            .queryParam("properties", "email,firstname,lastname")
                            .build(id))
                    .header("Authorization", "Bearer " + token())
                    .retrieve()
                    .bodyToMono(ContactResponse.class)
                    .blockOptional();
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        }
    }

    public Optional<ContactResponse> findByEmail(String email) {
        Map<String, Object> search = Map.of(
                "filterGroups", List.of(Map.of("filters", List.of(
                        Map.of("propertyName", "email", "operator", "EQ", "value", email)
                ))),
                "properties", List.of("email"),
                "limit", 1
        );

        ContactListResponse response = hubSpotWebClient.post()
                .uri("/crm/v3/objects/contacts/search")
                .header("Authorization", "Bearer " + token())
                .body(BodyInserters.fromValue(search))
                .retrieve()
                .bodyToMono(ContactListResponse.class)
                .block();

        return response != null && !response.getResults().isEmpty()
                ? Optional.of(response.getResults().get(0))
                : Optional.empty();
    }

    public void create(ContactCreateRequest request) {
        hubSpotWebClient.post()
                .uri("/crm/v3/objects/contacts")
                .header("Authorization", "Bearer " + token())
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void update(String id, ContactCreateRequest request) {
        hubSpotWebClient.patch()
                .uri("/crm/v3/objects/contacts/{id}", id)
                .header("Authorization", "Bearer " + token())
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ContactResponse getById(String id) {
        return hubSpotWebClient.get()
                .uri("/crm/v3/objects/contacts/{id}", id)
                .header("Authorization", "Bearer " + token())
                .retrieve()
                .bodyToMono(ContactResponse.class)
                .block();
    }

    public ContactListResponse getAll() {
        return hubSpotWebClient.get()
                .uri("/crm/v3/objects/contacts")
                .header("Authorization", "Bearer " + token())
                .retrieve()
                .bodyToMono(ContactListResponse.class)
                .block();
    }

    private String token() {
        return tokenStorage.get().getAccessToken();
    }
}