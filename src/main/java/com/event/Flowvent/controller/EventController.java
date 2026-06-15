package com.event.Flowvent.controller;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> events = eventService.listAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        EventResponseDto eventDto = eventService.getEventById(id);
        return ResponseEntity.ok(eventDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventCreateDto dto) {
        EventResponseDto updatedEvent = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateDto dto) {
        EventResponseDto newEvent = eventService.saveEvent(dto);
        return ResponseEntity.ok(newEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}