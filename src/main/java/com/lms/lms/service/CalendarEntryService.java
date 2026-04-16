package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarEntryService {

    private final CalendarEntryRepository calendarEntryRepository;
    private final EventRepository eventRepository;

    // ===== Personal Entries =====

    public List<CalendarEntry> findByOwner(User user) {
        return calendarEntryRepository.findByOwner(user);
    }

    public Optional<CalendarEntry> findById(Long id) {
        return calendarEntryRepository.findById(id);
    }

    @Transactional
    public CalendarEntry save(CalendarEntry entry) {
        return calendarEntryRepository.save(entry);
    }

    @Transactional
    public void delete(Long id) {
        calendarEntryRepository.deleteById(id);
    }

    // ===== Aggregated "visible entries" for a user in a date range =====

    /**
     * Returns all CalendarEntries visible to a user in [from, to]:
     * - Their own entries
     * - Shared teacher entries (if user is STUDENT and has a grade)
     */
    public List<CalendarEntry> findVisibleEntries(User user, LocalDate from, LocalDate to) {
        List<CalendarEntry> result = new ArrayList<>();

        // Personal entries
        result.addAll(calendarEntryRepository.findByOwnerAndStartDateBetween(user, from, to));

        // Shared teacher entries for students
        if (user.getRole() == User.Role.STUDENT && user.getGrade() != null) {
            result.addAll(calendarEntryRepository.findSharedEntriesForStudent(
                    user.getGrade().getId(), from, to));
        }

        return result;
    }

    /**
     * Returns admin Events visible to a user in [from, to]:
     * - Admins see all events
     * - Students/Teachers with a grade see all untargeted + their-grade-targeted events
     * - Users without a grade see all untargeted events
     */
    public List<Event> findVisibleAdminEvents(User user, LocalDate from, LocalDate to) {
        if (user.getRole() == User.Role.ADMIN) {
            return eventRepository.findByEventDateBetween(from, to);
        }
        Grade grade = (user.getRole() == User.Role.STUDENT)
                ? user.getGrade()
                : null; // Teachers don't have a single grade; show all untargeted events

        if (grade != null) {
            return eventRepository.findVisibleForGrade(grade.getId(), from, to);
        } else {
            // Show only events with no grade targeting
            return eventRepository.findByEventDateBetween(from, to).stream()
                    .filter(e -> e.getTargetGrades().isEmpty())
                    .toList();
        }
    }
}
