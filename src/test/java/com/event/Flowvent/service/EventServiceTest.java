package com.event.Flowvent.service;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.exception.EventNotFoundException;
import com.event.Flowvent.repository.EventRepository;
import com.event.Flowvent.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void saveEvent_shouldCreateEventSuccessfully() {
        EventCreateDto dto = new EventCreateDto();
        dto.setTitle("The Strokes Concert");
        dto.setDescription("Come enjoy the incredible songs of this band!");
        dto.setDate(LocalDate.now().plusDays(30));
        dto.setMaximumCapacity(100);
        dto.setTicketPrice(45.67);

        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setTitle(dto.getTitle());
        savedEvent.setDescription(dto.getDescription());
        savedEvent.setDate(dto.getDate());
        savedEvent.setMaximumCapacity(dto.getMaximumCapacity());
        savedEvent.setTicketPrice(dto.getTicketPrice());

        when(eventRepository.save(any(Event.class)))
                .thenReturn(savedEvent);

        when(ticketRepository.countByEventId(1L))
                .thenReturn(0L);

        EventResponseDto response = eventService.saveEvent(dto);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("The Strokes Concert");
        assertThat(response.getDescription()).isEqualTo("Come enjoy the incredible songs of this band!");
        assertThat(response.getMaximumCapacity()).isEqualTo(100);
        assertThat(response.getTicketPrice()).isEqualTo(45.67);
        assertThat(response.getSoldTickets()).isEqualTo(0L);
        assertThat(response.getAvailableTickets()).isEqualTo(100L);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getEventById_shouldReturnEvent_whenEventExists() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDescription("Concert description");
        event.setDate(LocalDate.now().plusDays(20));
        event.setMaximumCapacity(100);
        event.setTicketPrice(45.67);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.countByEventId(1L))
                .thenReturn(13L);

        EventResponseDto response = eventService.getEventById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("The Strokes Concert");
        assertThat(response.getSoldTickets()).isEqualTo(13L);
        assertThat(response.getAvailableTickets()).isEqualTo(87L);
    }

    @Test
    void getEventById_shouldThrowEventNotFoundException_whenEventDoesNotExist() {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(EventNotFoundException.class);

        verify(ticketRepository, never()).countByEventId(anyLong());
    }

    @Test
    void listAllEvents_shouldReturnPaginatedEventsWithAvailability() {
        Pageable pageable = PageRequest.of(0, 10);

        Event event = new Event();
        event.setId(1L);
        event.setTitle("The Strokes Concert");
        event.setDescription("Concert description");
        event.setDate(LocalDate.now().plusDays(20));
        event.setMaximumCapacity(100);
        event.setTicketPrice(45.67);

        Page<Event> eventPage = new PageImpl<>(List.of(event), pageable, 1);

        when(eventRepository.findAll(pageable))
                .thenReturn(eventPage);

        when(ticketRepository.countByEventId(1L))
                .thenReturn(20L);

        Page<EventResponseDto> response = eventService.listAllEvents(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getSoldTickets()).isEqualTo(20L);
        assertThat(response.getContent().get(0).getAvailableTickets()).isEqualTo(80L);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateEvent_shouldUpdateEventSuccessfully() {
        Event existentEvent = new Event();
        existentEvent.setId(1L);
        existentEvent.setTitle("Old title");
        existentEvent.setDescription("Old description");
        existentEvent.setDate(LocalDate.now().plusDays(10));
        existentEvent.setMaximumCapacity(50);
        existentEvent.setTicketPrice(20.0);

        EventCreateDto dto = new EventCreateDto();
        dto.setTitle("New title");
        dto.setDescription("New description");
        dto.setDate(LocalDate.now().plusDays(40));
        dto.setMaximumCapacity(200);
        dto.setTicketPrice(99.99);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(existentEvent));

        when(eventRepository.save(existentEvent))
                .thenReturn(existentEvent);

        when(ticketRepository.countByEventId(1L))
                .thenReturn(10L);

        EventResponseDto response = eventService.updateEvent(1L, dto);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("New title");
        assertThat(response.getDescription()).isEqualTo("New description");
        assertThat(response.getMaximumCapacity()).isEqualTo(200);
        assertThat(response.getTicketPrice()).isEqualTo(99.99);
        assertThat(response.getSoldTickets()).isEqualTo(10L);
        assertThat(response.getAvailableTickets()).isEqualTo(190L);

        verify(eventRepository).save(existentEvent);
    }
}