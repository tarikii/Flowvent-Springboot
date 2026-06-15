package com.event.Flowvent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateDto {

    @NotNull(message = "The client must exist in the data base of the website.")
    private Long clientId;

    @NotNull(message = "The event must exist in the data base of the website.")
    private Long eventId;

    private String seatNumber;
}
