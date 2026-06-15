package com.event.Flowvent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {
    private Long id;
    private String clientName;
    private String eventTitle;
    private String seat;
}
