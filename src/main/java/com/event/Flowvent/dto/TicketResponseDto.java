package com.event.Flowvent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {

    private Long id;

    private String clientName;
    private String clientEmail;

    private Long eventId;
    private String eventTitle;
    private LocalDate eventDate;

    private Integer seat;
    private Double ticketPrice;

    private LocalDateTime purchaseDate;
}