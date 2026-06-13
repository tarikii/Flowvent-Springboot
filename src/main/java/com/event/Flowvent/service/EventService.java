package com.event.Flowvent.service;

import com.event.Flowvent.model.Event;
import com.event.Flowvent.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public List<Event> listAllEvents() {
        return eventRepository.findAll();
    }

    public Event saveEvent(Event event){
        return eventRepository.save(event);
    }

    public Event findEventById(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));
    }

    public Event updateEvent(Long id, Event updatedEvent){
        Event existentEvent = findEventById(id);

        existentEvent.setTitle(updatedEvent.getTitle());
        existentEvent.setDescription(updatedEvent.getDescription());
        existentEvent.setDate(updatedEvent.getDate());
        existentEvent.setMaximumCapacity(updatedEvent.getMaximumCapacity());
        existentEvent.setTicketPrice(updatedEvent.getTicketPrice());
        return eventRepository.save(existentEvent);
    }

    public void deleteEvent(Long id){
        Event event = findEventById(id);
        eventRepository.delete(event);
    }
}
