package com.event.Flowvent.exception;

public class EventHasTicketsException extends RuntimeException {

    public EventHasTicketsException(Long eventId, long ticketsCount) {
        super("Cannot delete event with id " + eventId + " because it has " + ticketsCount + " ticket(s) linked to it");
    }
}