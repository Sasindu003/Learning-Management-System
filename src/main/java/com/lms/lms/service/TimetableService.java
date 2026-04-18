package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;

    public List<Timetable> findByGrade(Grade grade) {
        return timetableRepository.findByGradeOrderByDayOfWeekAscStartTimeAsc(grade);
    }

    public List<Timetable> findByTeacher(User teacher) {
        return timetableRepository.findByTeacherOrderByDayOfWeekAscStartTimeAsc(teacher);
    }

    public List<Timetable> findAll() {
        return timetableRepository.findAll();
    }

    public Optional<Timetable> findById(Long id) {
        return timetableRepository.findById(id);
    }

    public Timetable save(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    public List<String> validate(Timetable candidate) {
        List<String> errors = new ArrayList<>();

        if (candidate.getStartTime() == null || candidate.getEndTime() == null) {
            errors.add("Start and end time are required.");
            return errors;
        }

        if (!candidate.getEndTime().isAfter(candidate.getStartTime())) {
            errors.add("End time must be after start time.");
        }

        // Teacher overlap
        List<Timetable> teacherSlots = timetableRepository.findByTeacherAndDayOfWeek(candidate.getTeacher(), candidate.getDayOfWeek());
        for (Timetable existing : teacherSlots) {
            if (overlaps(candidate, existing)) {
                errors.add("Teacher " + candidate.getTeacher().getFullName() + " already has a class: " + 
                           existing.getSubject().getName() + " (" + existing.getStartTime() + "-" + existing.getEndTime() + ")");
                break;
            }
        }

        // Grade overlap
        List<Timetable> gradeSlots = timetableRepository.findByGradeAndDayOfWeek(candidate.getGrade(), candidate.getDayOfWeek());
        for (Timetable existing : gradeSlots) {
            if (overlaps(candidate, existing)) {
                errors.add("Grade " + candidate.getGrade().getName() + " already has a class: " + 
                           existing.getSubject().getName() + " (" + existing.getStartTime() + "-" + existing.getEndTime() + ")");
                break;
            }
        }

        // Room overlap
        if (candidate.getRoom() != null && !candidate.getRoom().trim().isEmpty()) {
            List<Timetable> roomSlots = timetableRepository.findByRoomAndDayOfWeek(candidate.getRoom(), candidate.getDayOfWeek());
            for (Timetable existing : roomSlots) {
                if (overlaps(candidate, existing)) {
                    errors.add("Room " + candidate.getRoom() + " is already occupied: " + 
                               existing.getSubject().getName() + " (" + existing.getStartTime() + "-" + existing.getEndTime() + ")");
                    break;
                }
            }
        }

        return errors;
    }

    private boolean overlaps(Timetable candidate, Timetable existing) {
        // Skip comparing with same record if editing (though current UI only does Add)
        if (candidate.getId() != null && candidate.getId().equals(existing.getId())) {
            return false;
        }
        return candidate.getStartTime().isBefore(existing.getEndTime()) && 
               candidate.getEndTime().isAfter(existing.getStartTime());
    }

    public void delete(Long id) {
        timetableRepository.deleteById(id);
    }
}
