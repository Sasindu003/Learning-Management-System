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

    public void delete(Long id) {
        timetableRepository.deleteById(id);
    }
}
