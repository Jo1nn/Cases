package com.integration.hubspot.contact.service;

import com.integration.hubspot.config.ContactFactory;
import com.integration.hubspot.contact.dto.ContactCreateRequest;
import com.integration.hubspot.contact.dto.ContactListResponse;
import com.integration.hubspot.contact.dto.ContactResponse;
import com.integration.hubspot.exception.ContactCreationException;
import com.integration.hubspot.exception.ContactFetchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private final ContactClient hubspotClient;
    private final ContactFactory contactFactory;

    public void createOrUpdateContact(ContactCreateRequest request) {
        try {
            Optional<ContactResponse> existing = hubspotClient.findByEmail(request.getProperties().getEmail());

            if (existing.isPresent()) {
                log.info("Contato encontrado. Atualizando ID: {}", existing.get().getId());
                hubspotClient.update(existing.get().getId(), request);
            } else {
                log.info("Contato não encontrado. Criando novo contato.");
                hubspotClient.create(request);
            }

        } catch (Exception e) {
            log.error("Erro ao criar ou atualizar contato: {}", e.getMessage(), e);
            throw new ContactCreationException("Erro ao criar ou atualizar contato", e);
        }
    }

    public void createContactFromHubSpot(Long id) {
        ContactResponse response = hubspotClient.fetchById(id)
                .orElseThrow(() -> new ContactFetchException("Contato não encontrado na HubSpot"));
        createOrUpdateContact(contactFactory.fromResponse(response));
    }

    public ContactResponse getContactById(String id) {
        return hubspotClient.getById(id);
    }

    public ContactListResponse getAllContacts() {
        return hubspotClient.getAll();
    }
}

