package com.event.Flowvent.service;

import com.event.Flowvent.dto.TicketCreateDto;
import com.event.Flowvent.dto.TicketResponseDto;
import com.event.Flowvent.dto.TicketUpdateDto;
import com.event.Flowvent.entity.Client;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.entity.Ticket;
import com.event.Flowvent.repository.ClientRepository;
import com.event.Flowvent.repository.EventRepository;
import com.event.Flowvent.repository.TicketRepository;
import org.springframework.stereotype.Service;

import com.event.Flowvent.exception.ClientNotFoundException;
import com.event.Flowvent.exception.EventNotFoundException;
import com.event.Flowvent.exception.EventFullException;
import com.event.Flowvent.exception.SeatAlreadyTakenException;
import com.event.Flowvent.exception.TicketNotFoundException;

import java.util.List;
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
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getClientId()));

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

        if (ticketRepository.existsByEventIdAndSeatNumber(existentTicket.getEvent().getId(), dto.getSeatNumber())) {
            throw new RuntimeException("The seat '" + dto.getSeatNumber() + "' is already occupied for this event.");
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
        ticketRepository.delete(ticket);
    }
}