package com.integration.hubspot.webhook.service;

import com.integration.hubspot.contact.service.ContactService;
import com.integration.hubspot.exception.ContactCreationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
    private final ContactService contactService;

    @Autowired
    public WebhookService(ContactService contactService) {
        this.contactService = contactService;
    }

    // Método para processar o webhook e criar um contato
    public void processWebhook(List<Map<String, Object>> webhookData) {
        try {
            for (Map<String, Object> event : webhookData) {
                if ("contact.creation".equals(event.get("subscriptionType"))) {
                    String contactId = String.valueOf(event.get("objectId"));
                    contactService.getContactById(contactId);
                    log.info("Contato processado via webhook com ID: {}", contactId);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage(), e);
            throw new ContactCreationException("Erro ao processar o webhook", e);
        }
    }
}
