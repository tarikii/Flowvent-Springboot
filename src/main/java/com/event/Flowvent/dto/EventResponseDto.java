package com.event.Flowvent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDto {
    private String title;
    private String description;
    private Integer maximumCapacity;
    private Double ticketPrice;
}
