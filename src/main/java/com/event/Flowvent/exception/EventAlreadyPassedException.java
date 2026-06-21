package com.event.Flowvent.exception;

public class EventAlreadyPassedException extends RuntimeException {

    public EventAlreadyPassedException(Long eventId) {
        super("Cannot buy tickets for past event with id: " + eventId);
    }
}
