package com.event.Flowvent.exception;

public class EventFullException extends RuntimeException {
    public EventFullException(Long eventId) {
        super("Event with id " + eventId + " is full");
    }
}
