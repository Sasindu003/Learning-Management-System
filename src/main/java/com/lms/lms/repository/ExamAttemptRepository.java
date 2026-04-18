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
}
