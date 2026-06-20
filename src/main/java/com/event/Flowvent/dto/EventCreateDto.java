package com.event.Flowvent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventCreateDto {

    @NotBlank(message = "The name of the event cannot be blank.")
    @Schema(example = "The Strokes Concert")
    private String title;

    @NotBlank(message = "The event cannot have a blank description.")
    @Schema(example = "Come enjoy the incredible songs of this band!")
    private String description;

    @NotNull(message = "The event date cannot be empty.")
    @Schema(example = "31/05/2029")
    private LocalDate date;

    @NotNull(message = "Capacity of the event must have a number of at least 50.")
    @Min(value = 50, message = "Capacity of the event must have a number of at least 50.")
    @Schema(example = "100")
    private Integer maximumCapacity;

    @NotNull(message = "The price of the ticket cannot be empty.")
    @Positive(message = "The price of the ticket must be above zero.")
    @Schema(example = "45,67")
    private Double ticketPrice;
}
