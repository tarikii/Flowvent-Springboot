package com.event.Flowvent.service;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.exception.EventNotFoundException;
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
}