package com.event.Flowvent.service;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.exception.EventNotFoundException;
import com.event.Flowvent.repository.EventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public List<EventResponseDto> listAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<EventResponseDto> listUpcomingEvents() {
        return eventRepository.findByDateAfterOrderByDateAsc(LocalDate.now()).stream()
                .map(event -> new EventResponseDto(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getMaximumCapacity(),
                        event.getTicketPrice()
                ))
                .collect(Collectors.toList());
    }

    public EventResponseDto saveEvent(EventCreateDto dto){
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setMaximumCapacity(dto.getMaximumCapacity());
        event.setTicketPrice(dto.getTicketPrice());

        Event savedEvent = eventRepository.save(event);

        return new EventResponseDto(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getDate(),
                savedEvent.getMaximumCapacity(),
                savedEvent.getTicketPrice()
        );
    }

    public Event findEventById(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public EventResponseDto getEventById(Long id) {
        Event event = findEventById(id);
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getMaximumCapacity(),
                event.getTicketPrice()
        );
    }

    public EventResponseDto updateEvent(Long id, EventCreateDto dto){
        Event existentEvent = findEventById(id);

        existentEvent.setTitle(dto.getTitle());
        existentEvent.setDescription(dto.getDescription());
        existentEvent.setDate(dto.getDate());
        existentEvent.setMaximumCapacity(dto.getMaximumCapacity());
        existentEvent.setTicketPrice(dto.getTicketPrice());

        Event updatedEvent = eventRepository.save(existentEvent);

        return new EventResponseDto(
                updatedEvent.getId(),
                updatedEvent.getTitle(),
                updatedEvent.getDescription(),
                updatedEvent.getDate(),
                updatedEvent.getMaximumCapacity(),
                updatedEvent.getTicketPrice()
        );
    }

    public void deleteEvent(Long id){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        eventRepository.delete(event);
    }

    private EventResponseDto mapToResponseDto(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getMaximumCapacity(),
                event.getTicketPrice()
        );
    }

    public List<EventResponseDto> searchEvents(
            String title,
            LocalDate fromDate,
            LocalDate toDate,
            Double minPrice,
            Double maxPrice
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

        return eventRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "date")).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
}