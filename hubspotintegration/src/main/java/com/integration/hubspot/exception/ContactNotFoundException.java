package com.integration.hubspot.exception;

public class ContactNotFoundException extends RuntimeException {

    // Construtores
    public ContactNotFoundException(String message) {
        super(message);
    }

    public ContactNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
