package com.lms.lms.repository;

import com.lms.lms.entity.Quiz;
import com.lms.lms.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QuizQuestionRepository — handles database operations for quiz questions.
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    /** Get all questions for a specific quiz */
    List<QuizQuestion> findByQuiz(Quiz quiz);
}
