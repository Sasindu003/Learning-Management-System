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
     * - Users see events if they are specifically targeted
     * - Users see events if they match Grade (if targeted) AND match Subject (if targeted)
     */
    public List<Event> findVisibleAdminEvents(User user, LocalDate from, LocalDate to) {
        List<Event> allInRange = eventRepository.findByEventDateBetweenOrderByEventDateAsc(from, to);
        if (user.getRole() == User.Role.ADMIN) {
            return allInRange;
        }

        return allInRange.stream().filter(e -> {
            // 1. If everyone is targeted
            if (e.getTargetTeachers().isEmpty() && e.getTargetGrades().isEmpty() && e.getTargetSubjects().isEmpty()) {
                return true;
            }

            // 2. If user is specifically targeted as a teacher
            if (e.getTargetTeachers().stream().anyMatch(t -> t.getId().equals(user.getId()))) {
                return true;
            }

            // 3. Category targeting (Intersectional: Grade AND Subject)
            boolean gradeMatch = true;
            if (!e.getTargetGrades().isEmpty()) {
                if (user.getRole() == User.Role.STUDENT) {
                    gradeMatch = user.getGrade() != null && e.getTargetGrades().contains(user.getGrade());
                } else if (user.getRole() == User.Role.TEACHER) {
                    // Match if any of the teacher's grades are targeted
                    gradeMatch = user.getGrades().stream().anyMatch(eg -> e.getTargetGrades().contains(eg));
                } else {
                    gradeMatch = false; // Other roles don't match grades
                }
            }

            boolean subjectMatch = true;
            if (!e.getTargetSubjects().isEmpty()) {
                // Match if any of the user's subjects are targeted
                subjectMatch = user.getSubjects().stream().anyMatch(es -> e.getTargetSubjects().contains(es));
            }

            return gradeMatch && subjectMatch;
        }).toList();
    }
}
