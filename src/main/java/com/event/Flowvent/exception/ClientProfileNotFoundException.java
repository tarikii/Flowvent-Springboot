package com.event.Flowvent.exception;

public class ClientProfileNotFoundException extends RuntimeException {
    public ClientProfileNotFoundException(String email) {
        super("Client profile not found for user: " + email);
    }
}
