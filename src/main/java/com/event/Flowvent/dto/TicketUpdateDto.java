package com.event.Flowvent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TicketUpdateDto {

    @Schema(example = "38")
    private Integer seatNumber;
}
