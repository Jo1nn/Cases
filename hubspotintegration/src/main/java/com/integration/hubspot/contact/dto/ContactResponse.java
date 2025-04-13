package com.integration.hubspot.contact.dto;

import lombok.Data;

@Data
public class ContactResponse {
    private String id;
    private ContactProperties properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContactProperties getProperties() {
        return properties;
    }

    public void setProperties(ContactProperties properties) {
        this.properties = properties;
    }
}
