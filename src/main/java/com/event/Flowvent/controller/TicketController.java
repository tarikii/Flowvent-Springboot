package com.event.Flowvent.controller;

import com.event.Flowvent.dto.TicketCreateDto;
import com.event.Flowvent.dto.TicketResponseDto;
import com.event.Flowvent.dto.TicketUpdateDto;
import com.event.Flowvent.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tickets", description = "Operations related to ticket purchases and seat management")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Gets you a list of all existent tickets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets found")
    })
    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {
        List<TicketResponseDto> tickets = ticketService.listAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get tickets owned by the authenticated client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<List<TicketResponseDto>> getMyTickets() {
        List<TicketResponseDto> tickets = ticketService.listMyTickets();
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get tickets for a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByEvent(@PathVariable Long eventId) {
        List<TicketResponseDto> tickets = ticketService.listTicketsByEvent(eventId);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Updates an existent ticket of a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "409", description = "Seat already taken")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> updateTicket(@PathVariable Long id, @Valid @RequestBody TicketUpdateDto dto) {
        TicketResponseDto updated = ticketService.updateTicketSeat(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Purchases a ticket for a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket purchased successfully"),
            @ApiResponse(responseCode = "404", description = "Client or event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Event full or seat already taken")
    })
    @PostMapping
    public ResponseEntity<TicketResponseDto> buyTicket(@Valid @RequestBody TicketCreateDto dto) {
        TicketResponseDto newTicket = ticketService.buyTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTicket);
    }

    @Operation(summary = "Delete an existing ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
