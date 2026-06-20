package com.event.Flowvent.exception;

public class SeatAlreadyTakenException extends RuntimeException {
    public SeatAlreadyTakenException(Integer seat) {
        super("Seat " + seat + " is already taken");
    }
}
