package com.lms.lms.repository;

import com.lms.lms.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByEventDateAsc();

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate date);

    List<Event> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);

    /**
     * Find admin events visible to a user with the given gradeId.
     * An event is visible if targetGrades is empty (all) OR contains the user's grade.
     */
    @Query("SELECT e FROM Event e " +
           "WHERE e.eventDate BETWEEN :from AND :to " +
           "AND (e.targetGrades IS EMPTY OR :gradeId IN (SELECT g.id FROM e.targetGrades g))")
    List<Event> findVisibleForGrade(
            @Param("gradeId") Long gradeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    // All events without grade filter (for admins or users with no grade)
    List<Event> findByEventDateBetween(LocalDate from, LocalDate to);
}
