package com.event.Flowvent.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventCreateDto {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDate date;

    @NotNull(message = "Maximum capacity is required")
    @Min(value = 1, message = "Maximum capacity must be at least 1")
    private Integer maximumCapacity;

    @NotNull(message = "Ticket price is required")
    @PositiveOrZero(message = "Ticket price cannot be negative")
    private Double ticketPrice;
}