package com.lms.lms.repository;

import com.lms.lms.model.Attendance;
import com.lms.lms.model.Course;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByCourseAndDate(Course course, LocalDate date);

    List<Attendance> findByStudentAndCourse(User student, Course course);

    List<Attendance> findByStudent(User student);

    Optional<Attendance> findByStudentAndCourseAndDate(User student, Course course, LocalDate date);

    long countByStudentAndCourseAndStatus(User student, Course course, Attendance.AttendanceStatus status);
}
