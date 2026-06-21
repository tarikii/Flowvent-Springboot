package com.event.Flowvent.controller;

import com.event.Flowvent.dto.EventCreateDto;
import com.event.Flowvent.dto.EventResponseDto;
import com.event.Flowvent.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Events", description = "Operations related to event management")
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Get all events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events found")
    })
    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<EventResponseDto> events = eventService.listAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get upcoming events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming events retrieved successfully")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponseDto>> getUpcomingEvents(
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<EventResponseDto> events = eventService.listUpcomingEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Search events using optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter format")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<EventResponseDto>> searchEvents(
            @RequestParam(required = false) String title,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,

            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<EventResponseDto> events = eventService.searchEvents(
                title,
                fromDate,
                toDate,
                minPrice,
                maxPrice,
                pageable
        );

        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get a specific event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        EventResponseDto eventDto = eventService.getEventById(id);
        return ResponseEntity.ok(eventDto);
    }

    @Operation(summary = "Creates a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateDto dto) {
        EventResponseDto newEvent = eventService.saveEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
    }

    @Operation(summary = "Updates an existing event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventCreateDto dto
    ) {
        EventResponseDto updatedEvent = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(summary = "Deletes an existing event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}