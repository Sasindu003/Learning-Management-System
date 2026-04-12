package com.lms.lms.repository;

import com.lms.lms.model.ExamQuestion;
import com.lms.lms.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByExamOrderByQuestionOrderAsc(Exam exam);

    long countByExam(Exam exam);
}
