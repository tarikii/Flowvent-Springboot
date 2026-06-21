package com.event.Flowvent.repository;

import com.event.Flowvent.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByDateAfterOrderByDateAsc(LocalDate date);
    Page<Event> findByDateAfter(LocalDate date, Pageable pageable);
}