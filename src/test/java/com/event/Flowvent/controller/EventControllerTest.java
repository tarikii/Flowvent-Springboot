package com.event.Flowvent.controller;

import com.event.Flowvent.exception.GlobalExceptionHandler;
import com.event.Flowvent.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EventController eventController = new EventController(eventService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createEvent_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        String invalidRequest = """
                {
                  "title": "",
                  "description": "",
                  "date": "2020-01-01",
                  "maximumCapacity": 0,
                  "ticketPrice": -10
                }
                """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.messages.title").value("Title is required"))
                .andExpect(jsonPath("$.messages.description").value("Description is required"))
                .andExpect(jsonPath("$.messages.date").value("Event date must be in the future"))
                .andExpect(jsonPath("$.messages.maximumCapacity").value("Maximum capacity must be at least 1"))
                .andExpect(jsonPath("$.messages.ticketPrice").value("Ticket price cannot be negative"));

        verify(eventService, never()).saveEvent(any());
    }
}