package com.event.Flowvent.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateDto {

    @NotNull(message = "Event id is required")
    private Long eventId;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be at least 1")
    private Integer seatNumber;
}