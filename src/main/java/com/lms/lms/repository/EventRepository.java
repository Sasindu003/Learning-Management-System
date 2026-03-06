package com.lms.lms.repository;

import com.lms.lms.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByEventDateAsc();

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate date);

    List<Event> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);
}
