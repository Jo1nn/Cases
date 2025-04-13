package com.integration.hubspot.exception;

import java.awt.event.FocusEvent;

public class ContactCreationException extends RuntimeException {

    public ContactCreationException(String message) {
        super(message);
    }

    public ContactCreationException(String message, Exception cause) {
        super(message, cause);
    }
}