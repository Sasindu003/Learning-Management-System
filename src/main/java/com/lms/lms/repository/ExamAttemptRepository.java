package com.lms.lms.repository;

import com.lms.lms.model.ExamAttempt;
import com.lms.lms.model.Exam;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    Optional<ExamAttempt> findByExamAndStudent(Exam exam, User student);

    List<ExamAttempt> findByStudent(User student);

    List<ExamAttempt> findByExam(Exam exam);

    boolean existsByExamAndStudent(Exam exam, User student);

    long countByExamAndCompleted(Exam exam, boolean completed);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM ExamAttempt e WHERE e.exam.course.teacher = :teacher AND e.student.grade IN :grades AND e.completed = true ORDER BY e.endTime DESC")
    List<ExamAttempt> findRecentByTeacherAndGrades(
        @org.springframework.data.repository.query.Param("teacher") com.lms.lms.model.User teacher, 
        @org.springframework.data.repository.query.Param("grades") Set<com.lms.lms.model.Grade> grades, 
        org.springframework.data.domain.Pageable pageable
    );
}
