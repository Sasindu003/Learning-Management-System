package com.lms.lms.repository;

import com.lms.lms.model.ExamAnswer;
import com.lms.lms.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {
    List<ExamAnswer> findByAttempt(ExamAttempt attempt);
}
