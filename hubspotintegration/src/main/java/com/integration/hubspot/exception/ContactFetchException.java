package com.integration.hubspot.exception;

public class ContactFetchException extends RuntimeException {

    public ContactFetchException(String message) {
        super(message);
    }

    public ContactFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
