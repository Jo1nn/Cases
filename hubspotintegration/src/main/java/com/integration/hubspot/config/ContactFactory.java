package com.integration.hubspot.config;

import com.integration.hubspot.contact.dto.ContactCreateRequest;
import com.integration.hubspot.contact.dto.ContactResponse;
import org.springframework.stereotype.Component;

@Component
public class ContactFactory {
    public ContactCreateRequest fromResponse(ContactResponse response) {
        return new ContactCreateRequest(response.getProperties());
    }
}
