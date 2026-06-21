package com.event.Flowvent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateDto {

    @NotNull(message = "The event must exist in the data base of the website.")
    @Schema(example = "3")
    private Long eventId;

    @Schema(example = "34")
    private Integer seatNumber;
}
