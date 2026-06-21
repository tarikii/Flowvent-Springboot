package com.event.Flowvent.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You do not have permission to access this resource");
    }
}
