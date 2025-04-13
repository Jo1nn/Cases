package com.integration.hubspot.contact.dto;

public class ContactCreateRequest {

    private ContactProperties properties;

    public ContactProperties getProperties() {
        return properties;
    }

    public void setProperties(ContactProperties properties) {
        this.properties = properties;
    }

    public ContactCreateRequest(ContactProperties properties) {
        this.properties = properties;
    }
}
