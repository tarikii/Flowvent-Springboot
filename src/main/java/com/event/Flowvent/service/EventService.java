package com.event.Flowvent.service;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.exception.EventNotFoundException;
import com.event.Flowvent.repository.EventRepository;
import com.event.Flowvent.repository.TicketRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public EventService(EventRepository eventRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    public Page<EventResponseDto> listAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    public Page<EventResponseDto> listUpcomingEvents(Pageable pageable) {
        return eventRepository.findByDateAfter(LocalDate.now(), pageable)
                .map(this::mapToResponseDto);
    }

    public EventResponseDto saveEvent(EventCreateDto dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setMaximumCapacity(dto.getMaximumCapacity());
        event.setTicketPrice(dto.getTicketPrice());

        Event savedEvent = eventRepository.save(event);

        return mapToResponseDto(savedEvent);
    }

    public Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public EventResponseDto getEventById(Long id) {
        Event event = findEventById(id);
        return mapToResponseDto(event);
    }

    public EventResponseDto updateEvent(Long id, EventCreateDto dto) {
        Event existentEvent = findEventById(id);

        existentEvent.setTitle(dto.getTitle());
        existentEvent.setDescription(dto.getDescription());
        existentEvent.setDate(dto.getDate());
        existentEvent.setMaximumCapacity(dto.getMaximumCapacity());
        existentEvent.setTicketPrice(dto.getTicketPrice());

        Event updatedEvent = eventRepository.save(existentEvent);

        return mapToResponseDto(updatedEvent);
    }

    public void deleteEvent(Long id) {
        Event event = findEventById(id);
        eventRepository.delete(event);
    }

    public Page<EventResponseDto> searchEvents(
            String title,
            LocalDate fromDate,
            LocalDate toDate,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    ) {
        Specification<Event> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();

        if (title != null && !title.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            "%" + title.toLowerCase() + "%"
                    )
            );
        }

        if (fromDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.<LocalDate>get("date"),
                            fromDate
                    )
            );
        }

        if (toDate != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(
                            root.<LocalDate>get("date"),
                            toDate
                    )
            );
        }

        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.<Double>get("ticketPrice"),
                            minPrice
                    )
            );
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(
                            root.<Double>get("ticketPrice"),
                            maxPrice
                    )
            );
        }

        return eventRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    private EventResponseDto mapToResponseDto(Event event) {
        long soldTickets = ticketRepository.countByEventId(event.getId());
        long availableTickets = Math.max(0L, event.getMaximumCapacity() - soldTickets);

        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getMaximumCapacity(),
                event.getTicketPrice(),
                soldTickets,
                availableTickets
        );
    }
}