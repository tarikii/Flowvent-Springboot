package com.event.Flowvent.service;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.repository.EventRepository;
import org.springframework.stereotype.Service;

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
                .map(event -> new EventResponseDto(
                        event.getTitle(),
                        event.getDescription(),
                        event.getMaximumCapacity(),
                        event.getTicketPrice()
                ))
                .collect(Collectors.toList());
    }

    public EventResponseDto saveEvent(EventCreateDto dto){
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setMaximumCapacity(dto.getMaximumCapacity());
        event.setTicketPrice(dto.getTicketPrice());

        Event savedEvent = eventRepository.save(event);

        return new EventResponseDto(
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getMaximumCapacity(),
                savedEvent.getTicketPrice()
        );
    }

    public Event findEventById(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));
    }

    public EventResponseDto getEventById(Long id) {
        Event event = findEventById(id);
        return new EventResponseDto(
                event.getTitle(),
                event.getDescription(),
                event.getMaximumCapacity(),
                event.getTicketPrice()
        );
    }

    public EventResponseDto updateEvent(Long id, EventCreateDto dto){
        Event existentEvent = findEventById(id);

        existentEvent.setTitle(dto.getTitle());
        existentEvent.setDescription(dto.getDescription());
        existentEvent.setMaximumCapacity(dto.getMaximumCapacity());
        existentEvent.setTicketPrice(dto.getTicketPrice());

        Event updatedEvent = eventRepository.save(existentEvent);

        return new EventResponseDto(
                updatedEvent.getTitle(),
                updatedEvent.getDescription(),
                updatedEvent.getMaximumCapacity(),
                updatedEvent.getTicketPrice()
        );
    }

    public void deleteEvent(Long id){
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Event not found with ID: " + id);
        }
        eventRepository.deleteById(id);
    }
}