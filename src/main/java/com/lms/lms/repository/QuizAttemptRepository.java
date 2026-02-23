package com.lms.lms.repository;

import com.lms.lms.entity.Quiz;
import com.lms.lms.entity.QuizAttempt;
import com.lms.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * QuizAttemptRepository — handles database operations for quiz attempts.
 * (Updated to trigger re-index)
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /** Get all attempts for a quiz — teacher sees all student results */
    List<QuizAttempt> findByQuiz(Quiz quiz);

    /** Check if a student already attempted a quiz (prevent retakes) */
    Optional<QuizAttempt> findByQuizAndStudent(Quiz quiz, User student);

    /** Get all attempts by a student */
    List<QuizAttempt> findByStudent(User student);
}
