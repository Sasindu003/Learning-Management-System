package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository questionRepository;
    private final ExamAttemptRepository attemptRepository;
    private final ExamAnswerRepository answerRepository;

    public List<Exam> findByCourse(Course course) {
        return examRepository.findByCourseOrderByExamDateDesc(course);
    }

    public List<Exam> findPublishedByCourses(List<Course> courses) {
        return examRepository.findByCourseInAndPublished(courses, true);
    }

    public List<Exam> findByCourses(List<Course> courses) {
        return examRepository.findByCourseIn(courses);
    }

    public Optional<Exam> findById(Long id) {
        return examRepository.findById(id);
    }

    @Transactional
    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    @Transactional
    public void delete(Long id) {
        examRepository.deleteById(id);
    }

    // Questions
    public List<ExamQuestion> getQuestions(Exam exam) {
        return questionRepository.findByExamOrderByQuestionOrderAsc(exam);
    }

    @Transactional
    public ExamQuestion saveQuestion(ExamQuestion question) {
        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    // Attempts
    public boolean hasAttempted(Exam exam, User student) {
        return attemptRepository.existsByExamAndStudent(exam, student);
    }

    public Optional<ExamAttempt> getAttempt(Exam exam, User student) {
        return attemptRepository.findByExamAndStudent(exam, student);
    }

    public List<ExamAttempt> getAttempts(Exam exam) {
        return attemptRepository.findByExam(exam);
    }

    public List<ExamAttempt> getStudentAttempts(User student) {
        return attemptRepository.findByStudent(student);
    }

    @Transactional
    public ExamAttempt startAttempt(Exam exam, User student) {
        if (attemptRepository.existsByExamAndStudent(exam, student)) {
            throw new RuntimeException("Already attempted this exam");
        }
        ExamAttempt attempt = ExamAttempt.builder()
                .exam(exam)
                .student(student)
                .totalMarks(exam.getTotalMarks())
                .build();
        return attemptRepository.save(attempt);
    }

    @Transactional
    public ExamAttempt submitAttempt(Long attemptId, Map<Long, String> answers) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        int score = 0;
        List<ExamQuestion> questions = questionRepository.findByExamOrderByQuestionOrderAsc(attempt.getExam());

        for (ExamQuestion question : questions) {
            String selected = answers.get(question.getId());
            boolean correct = selected != null && selected.equalsIgnoreCase(question.getCorrectAnswer());

            ExamAnswer answer = ExamAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .selectedAnswer(selected)
                    .correct(correct)
                    .marksAwarded(correct ? question.getMarks() : 0)
                    .build();
            answerRepository.save(answer);

            if (correct)
                score += question.getMarks();
        }

        attempt.setScore(score);
        attempt.setCompleted(true);
        attempt.setEndTime(LocalDateTime.now());
        if (attempt.getTotalMarks() > 0) {
            attempt.setPercentage((double) score / attempt.getTotalMarks() * 100);
        }
        return attemptRepository.save(attempt);
    }
}
