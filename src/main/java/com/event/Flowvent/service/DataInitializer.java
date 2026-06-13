package com.event.Flowvent.service;

import com.event.Flowvent.model.Event;
import com.event.Flowvent.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;

    public DataInitializer(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() == 0) {
            Event event1 = new Event();
            event1.setTitle("Concierto de Rock de Verano");
            event1.setDescription("El festival de rock más esperado del año.");
            event1.setDate(LocalDate.of(2026, 7, 15));
            event1.setMaximumCapacity(500);
            event1.setTicketPrice(25.50);
            eventRepository.save(event1);

            Event event2 = new Event();
            event2.setTitle("Concierto de Rock de Invierno");
            event2.setDescription("El festival de rock menos esperado del año.");
            event2.setDate(LocalDate.of(2026, 2, 21));
            event2.setMaximumCapacity(1000);
            event2.setTicketPrice(65.50);
            eventRepository.save(event2);

            System.out.println("¡Base de datos inicializada con DOS eventos!");
        }
    }
}
