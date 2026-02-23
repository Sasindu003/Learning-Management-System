package com.lms.lms.repository;

import com.lms.lms.entity.Quiz;
import com.lms.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QuizRepository — handles database operations for quizzes.
 * (Updated to trigger re-index)
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /** Get quizzes for a specific subject */
    List<Quiz> findBySubject(Subject subject);
}
