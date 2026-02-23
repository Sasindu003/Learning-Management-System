package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * QuizAttempt entity — records a student's attempt at a quiz.
 * (Updated to trigger re-index)
 *
 * Stores the score and total questions so we can show results like "3 out of
 * 5".
 * Each student can only have one attempt per quiz (enforced in the controller).
 */
@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** How many questions the student got right */
    private int score;

    /** Total questions in the quiz at the time of attempt */
    private int totalQuestions;

    /** Which quiz was attempted? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    /** Which student took this quiz? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** When the quiz was submitted */
    private LocalDateTime attemptedAt;

    @PrePersist
    protected void onCreate() {
        this.attemptedAt = LocalDateTime.now();
    }
}
