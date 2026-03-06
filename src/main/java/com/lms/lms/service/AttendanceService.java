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
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> findByCourseAndDate(Course course, LocalDate date) {
        return attendanceRepository.findByCourseAndDate(course, date);
    }

    public List<Attendance> findByStudent(User student) {
        return attendanceRepository.findByStudent(student);
    }

    @Transactional
    public Attendance save(Attendance attendance) {
        Optional<Attendance> existing = attendanceRepository
                .findByStudentAndCourseAndDate(attendance.getStudent(), attendance.getCourse(), attendance.getDate());
        if (existing.isPresent()) {
            Attendance a = existing.get();
            a.setStatus(attendance.getStatus());
            a.setRemarks(attendance.getRemarks());
            return attendanceRepository.save(a);
        }
        return attendanceRepository.save(attendance);
    }

    @Transactional
    public void saveAll(List<Attendance> records) {
        for (Attendance record : records) {
            save(record);
        }
    }

    public Map<String, Long> getStats(User student, Course course) {
        Map<String, Long> stats = new HashMap<>();
        for (Attendance.AttendanceStatus status : Attendance.AttendanceStatus.values()) {
            stats.put(status.name(), attendanceRepository.countByStudentAndCourseAndStatus(student, course, status));
        }
        return stats;
    }
}
