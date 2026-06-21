package com.event.Flowvent.repository;

import com.event.Flowvent.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    long countByEventId(Long eventId);

    boolean existsByEventIdAndSeatNumber(Long eventId, Integer seatNumber);

    Page<Ticket> findByClientUserEmail(String email, Pageable pageable);

    Page<Ticket> findByEventId(Long eventId, Pageable pageable);
}