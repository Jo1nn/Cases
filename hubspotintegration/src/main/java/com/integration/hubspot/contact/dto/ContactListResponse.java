package com.integration.hubspot.contact.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContactListResponse {
    private List<ContactResponse> results;

    public List<ContactResponse> getResults() {
        return results;
    }

    public void setResults(List<ContactResponse> results) {
        this.results = results;
    }
}
