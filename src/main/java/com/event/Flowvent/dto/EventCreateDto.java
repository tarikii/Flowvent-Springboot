package com.event.Flowvent.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EventCreateDto {

    @NotBlank(message = "The name of the event cannot be blank.")
    private String title;

    @NotBlank(message = "The event cannot have a blank description.")
    private String description;

    @NotNull(message = "Capacity of the event must have a number of at least 50.")
    @Min(value = 50, message = "Capacity of the event must have a number of at least 50.")
    private Integer maximumCapacity;

    @NotNull(message = "The price of the ticket cannot be empty.")
    @Positive(message = "The price of the ticket must be above zero.")
    private Double ticketPrice;
}
