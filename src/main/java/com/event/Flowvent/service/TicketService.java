package com.event.Flowvent.service;

import com.event.Flowvent.dto.TicketCreateDto;
import com.event.Flowvent.dto.TicketResponseDto;
import com.event.Flowvent.dto.TicketUpdateDto;
import com.event.Flowvent.entity.Client;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.entity.Ticket;
import com.event.Flowvent.exception.*;
import com.event.Flowvent.repository.ClientRepository;
import com.event.Flowvent.repository.EventRepository;
import com.event.Flowvent.repository.TicketRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final EventRepository eventRepository;

    public TicketService(TicketRepository ticketRepository, ClientRepository clientRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.clientRepository = clientRepository;
        this.eventRepository = eventRepository;
    }

    public Ticket findTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public TicketResponseDto buyTicket(TicketCreateDto dto) {
        String authenticatedEmail = getAuthenticatedUserEmail();

        Client client = clientRepository.findByUserEmail(authenticatedEmail)
                .orElseThrow(() -> new ClientProfileNotFoundException(authenticatedEmail));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EventNotFoundException(dto.getEventId()));

        long soldTickets = ticketRepository.countByEventId(dto.getEventId());
        if (soldTickets >= event.getMaximumCapacity()) {
            throw new EventFullException(event.getId());
        }

        Integer finalSeat = dto.getSeatNumber();

        if (ticketRepository.existsByEventIdAndSeatNumber(dto.getEventId(), finalSeat)) {
            throw new SeatAlreadyTakenException(finalSeat);
        }

        Ticket ticket = new Ticket();
        ticket.setClient(client);
        ticket.setEvent(event);
        ticket.setSeatNumber(finalSeat);

        Ticket savedTicket = ticketRepository.save(ticket);

        return new TicketResponseDto(
                savedTicket.getId(),
                savedTicket.getClient().getName(),
                savedTicket.getEvent().getTitle(),
                savedTicket.getSeatNumber()
        );
    }

    public TicketResponseDto updateTicketSeat(Long id, TicketUpdateDto dto) {
        Ticket existentTicket = findTicketById(id);

        validateTicketOwnership(existentTicket);

        if (ticketRepository.existsByEventIdAndSeatNumber(existentTicket.getEvent().getId(), dto.getSeatNumber())) {
            throw new SeatAlreadyTakenException(dto.getSeatNumber());
        }

        existentTicket.setSeatNumber(dto.getSeatNumber());
        Ticket updatedTicket = ticketRepository.save(existentTicket);

        return new TicketResponseDto(
                updatedTicket.getId(),
                updatedTicket.getClient().getName(),
                updatedTicket.getEvent().getTitle(),
                updatedTicket.getSeatNumber()
        );
    }

    public List<TicketResponseDto> listAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticket -> new TicketResponseDto(
                        ticket.getId(),
                        ticket.getClient().getName(),
                        ticket.getEvent().getTitle(),
                        ticket.getSeatNumber()
                ))
                .collect(Collectors.toList());
    }

    public void deleteTicket(Long id) {
        Ticket ticket = findTicketById(id);
        validateTicketOwnership(ticket);

        ticketRepository.delete(ticket);
    }

    private String getAuthenticatedUserEmail() {
        return Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();
    }

    private void validateTicketOwnership(Ticket ticket) {
        String authenticatedEmail = getAuthenticatedUserEmail();

        boolean isOwner = ticket.getClient()
                .getEmail()
                .equals(authenticatedEmail);

        boolean isAdmin = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException();
        }
    }
}