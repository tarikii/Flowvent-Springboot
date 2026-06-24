package com.event.Flowvent.service;

import com.event.Flowvent.dto.TicketCreateDto;
import com.event.Flowvent.dto.TicketResponseDto;
import com.event.Flowvent.dto.TicketUpdateDto;
import com.event.Flowvent.entity.Client;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.entity.Ticket;
import com.event.Flowvent.exception.*;
import com.event.Flowvent.user.User;
import com.event.Flowvent.repository.ClientRepository;
import com.event.Flowvent.repository.EventRepository;
import com.event.Flowvent.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("client@flowvent.com", null)
        );
    }

    @Test
    void buyTicket_shouldCreateTicket_whenDataIsValid() {
        TicketCreateDto dto = new TicketCreateDto();
        dto.setEventId(1L);
        dto.setSeatNumber(12);

        User user = new User();
        user.setId(1L);
        user.setEmail("client@flowvent.com");

        Client client = new Client();
        client.setId(1L);
        client.setName("client");
        client.setEmail("client@flowvent.com");
        client.setUser(user);

        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDescription("Concert description");
        event.setDate(LocalDate.now().plusDays(10));
        event.setMaximumCapacity(100);
        event.setTicketPrice(45.67);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setClient(client);
        savedTicket.setEvent(event);
        savedTicket.setSeatNumber(12);

        when(clientRepository.findByUserEmail("client@flowvent.com"))
                .thenReturn(Optional.of(client));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.countByEventId(1L))
                .thenReturn(0L);

        when(ticketRepository.existsByEventIdAndSeatNumber(1L, 12))
                .thenReturn(false);

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    Ticket ticket = invocation.getArgument(0);
                    ticket.setId(1L);
                    return ticket;
                });

        TicketResponseDto response = ticketService.buyTicket(dto);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getClientEmail()).isEqualTo("client@flowvent.com");
        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getEventTitle()).isEqualTo("The Strokes Concert");
        assertThat(response.getSeat()).isEqualTo(12);
        assertThat(response.getTicketPrice()).isEqualTo(45.67);

        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void buyTicket_shouldThrowEventAlreadyPassedException_whenEventDateIsPast() {
        TicketCreateDto dto = new TicketCreateDto();
        dto.setEventId(1L);
        dto.setSeatNumber(12);

        Client client = new Client();
        client.setId(1L);
        client.setName("client");
        client.setEmail("client@flowvent.com");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Past Concert");
        event.setDate(LocalDate.now().minusDays(1));
        event.setMaximumCapacity(100);
        event.setTicketPrice(45.67);

        when(clientRepository.findByUserEmail("client@flowvent.com"))
                .thenReturn(Optional.of(client));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        assertThatThrownBy(() -> ticketService.buyTicket(dto))
                .isInstanceOf(EventAlreadyPassedException.class)
                .hasMessage("Cannot buy tickets for past event with id: 1");

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void buyTicket_shouldThrowEventFullException_whenEventIsFull() {
        TicketCreateDto dto = new TicketCreateDto();
        dto.setEventId(1L);
        dto.setSeatNumber(12);

        Client client = new Client();
        client.setId(1L);
        client.setName("client");
        client.setEmail("client@flowvent.com");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDate(LocalDate.now().plusDays(10));
        event.setMaximumCapacity(100);
        event.setTicketPrice(45.67);

        when(clientRepository.findByUserEmail("client@flowvent.com"))
                .thenReturn(Optional.of(client));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.countByEventId(1L))
                .thenReturn(100L);

        assertThatThrownBy(() -> ticketService.buyTicket(dto))
                .isInstanceOf(EventFullException.class)
                .hasMessage("Event with id 1 is full");

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void buyTicket_shouldThrowSeatAlreadyTakenException_whenSeatIsAlreadyTaken() {
        TicketCreateDto dto = new TicketCreateDto();
        dto.setEventId(1L);
        dto.setSeatNumber(12);

        Client client = new Client();
        client.setId(1L);
        client.setName("client");
        client.setEmail("client@flowvent.com");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDate(LocalDate.now().plusDays(10));
        event.setMaximumCapacity(100);
        event.setTicketPrice(45.67);

        when(clientRepository.findByUserEmail("client@flowvent.com"))
                .thenReturn(Optional.of(client));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.countByEventId(1L))
                .thenReturn(10L);

        when(ticketRepository.existsByEventIdAndSeatNumber(1L, 12))
                .thenReturn(true);

        assertThatThrownBy(() -> ticketService.buyTicket(dto))
                .isInstanceOf(SeatAlreadyTakenException.class);

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void buyTicket_shouldThrowClientProfileNotFoundException_whenAuthenticatedUserHasNoClientProfile() {
        TicketCreateDto dto = new TicketCreateDto();
        dto.setEventId(1L);
        dto.setSeatNumber(12);

        when(clientRepository.findByUserEmail("client@flowvent.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.buyTicket(dto))
                .isInstanceOf(ClientProfileNotFoundException.class)
                .hasMessage("Client profile not found for user: client@flowvent.com");

        verify(eventRepository, never()).findById(anyLong());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void updateTicketSeat_shouldNotThrow_whenSeatIsTheSame() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDate(LocalDate.now().plusDays(10));
        event.setTicketPrice(45.67);

        Client client = new Client();
        client.setId(1L);
        client.setName("client");
        client.setEmail("client@flowvent.com");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setClient(client);
        ticket.setEvent(event);
        ticket.setSeatNumber(12);
        ticket.setPurchaseDate(LocalDateTime.now());

        TicketUpdateDto dto = new TicketUpdateDto();
        dto.setSeatNumber(12);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(ticket);

        TicketResponseDto response = ticketService.updateTicketSeat(1L, dto);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSeat()).isEqualTo(12);

        verify(ticketRepository, never())
                .existsByEventIdAndSeatNumber(anyLong(), anyInt());

        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateTicketSeat_shouldThrowSeatAlreadyTakenException_whenNewSeatIsAlreadyTaken() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDate(LocalDate.now().plusDays(10));
        event.setTicketPrice(45.67);

        Client client = new Client();
        client.setId(1L);
        client.setName("client");
        client.setEmail("client@flowvent.com");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setClient(client);
        ticket.setEvent(event);
        ticket.setSeatNumber(12);
        ticket.setPurchaseDate(LocalDateTime.now());

        TicketUpdateDto dto = new TicketUpdateDto();
        dto.setSeatNumber(15);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.existsByEventIdAndSeatNumber(1L, 15))
                .thenReturn(true);

        assertThatThrownBy(() -> ticketService.updateTicketSeat(1L, dto))
                .isInstanceOf(SeatAlreadyTakenException.class);

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void updateTicketSeat_shouldThrowAccessDeniedException_whenTicketBelongsToAnotherClient() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDate(LocalDate.now().plusDays(10));
        event.setTicketPrice(45.67);

        Client anotherClient = new Client();
        anotherClient.setId(2L);
        anotherClient.setName("another client");
        anotherClient.setEmail("another@flowvent.com");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setClient(anotherClient);
        ticket.setEvent(event);
        ticket.setSeatNumber(12);
        ticket.setPurchaseDate(LocalDateTime.now());

        TicketUpdateDto dto = new TicketUpdateDto();
        dto.setSeatNumber(15);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> ticketService.updateTicketSeat(1L, dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(ticketRepository, never()).existsByEventIdAndSeatNumber(anyLong(), anyInt());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}