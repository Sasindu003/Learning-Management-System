package com.lms.lms.repository;

import com.lms.lms.model.CalendarEntry;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {

    List<CalendarEntry> findByOwner(User owner);

    List<CalendarEntry> findByOwnerAndStartDateBetween(User owner, LocalDate from, LocalDate to);

    /**
     * Find shared teacher entries visible to a student with a given gradeId.
     * An entry is visible if:
     * - It is shared
     * - The owner is a TEACHER
     * - sharedGrades is empty (visible to all teacher's grades) OR contains the student's grade
     */
    @Query("SELECT e FROM CalendarEntry e " +
           "JOIN e.owner u " +
           "WHERE e.shared = true " +
           "AND u.role = 'TEACHER' " +
           "AND (e.sharedGrades IS EMPTY OR :gradeId IN (SELECT g.id FROM e.sharedGrades g)) " +
           "AND e.startDate BETWEEN :from AND :to")
    List<CalendarEntry> findSharedEntriesForStudent(
            @Param("gradeId") Long gradeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
