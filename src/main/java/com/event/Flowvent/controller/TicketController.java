package com.event.Flowvent.controller;

import com.event.Flowvent.dto.TicketCreateDto;
import com.event.Flowvent.dto.TicketResponseDto;
import com.event.Flowvent.dto.TicketUpdateDto;
import com.event.Flowvent.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {
        List<TicketResponseDto> tickets = ticketService.listAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> updateTicket(@PathVariable Long id, @Valid @RequestBody TicketUpdateDto dto) {
        TicketResponseDto updated = ticketService.updateTicketSeat(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> buyTicket(@Valid @RequestBody TicketCreateDto dto) {
        TicketResponseDto newTicket = ticketService.buyTicket(dto);
        return ResponseEntity.ok(newTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
