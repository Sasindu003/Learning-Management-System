package com.lms.lms.service;

import com.lms.lms.entity.*;
import com.lms.lms.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * QuizService — business logic for quizzes, questions, and attempts.
 *
 * Consolidates quiz-related operations into one service for simplicity.
 * In a larger app, you might split this into QuizService + QuizAttemptService.
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;

    public QuizService(QuizRepository quizRepository,
            QuizQuestionRepository questionRepository,
            QuizAttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
    }

    // ==================== QUIZ CRUD ====================

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> findQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    // ==================== QUESTIONS ====================

    public List<QuizQuestion> getQuestionsByQuiz(Quiz quiz) {
        return questionRepository.findByQuiz(quiz);
    }

    public QuizQuestion saveQuestion(QuizQuestion question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    // ==================== ATTEMPTS ====================

    /** Check if a student already took this quiz */
    public Optional<QuizAttempt> findAttempt(Quiz quiz, User student) {
        return attemptRepository.findByQuizAndStudent(quiz, student);
    }

    /** Get all attempts for a quiz — teacher views results */
    public List<QuizAttempt> getAttemptsByQuiz(Quiz quiz) {
        return attemptRepository.findByQuiz(quiz);
    }

    /** Save a quiz attempt (student's score) */
    public QuizAttempt saveAttempt(QuizAttempt attempt) {
        return attemptRepository.save(attempt);
    }

    /**
     * Grade a quiz by comparing student answers to correct answers.
     *
     * @param questions The quiz questions
     * @param answers   The student's answers (indexed by question ID)
     * @return The number of correct answers
     */
    public int gradeQuiz(List<QuizQuestion> questions, java.util.Map<Long, String> answers) {
        int score = 0;
        for (QuizQuestion question : questions) {
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer != null && studentAnswer.equalsIgnoreCase(question.getCorrectOption())) {
                score++;
            }
        }
        return score;
    }
}
