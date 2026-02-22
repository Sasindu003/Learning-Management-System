package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * QuizQuestion entity — a single multiple-choice question within a quiz.
 *
 * Simple model: 4 options (A, B, C, D) and one correct answer.
 * The correctOption field stores which option is right ("A", "B", "C", or "D").
 *
 * @ToString.Exclude on 'quiz' prevents infinite loops since Quiz has a list of
 *                   questions.
 */
@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText; // e.g., "What is 2 + 2?"

    @Column(nullable = false)
    private String optionA; // e.g., "3"

    @Column(nullable = false)
    private String optionB; // e.g., "4"

    @Column(nullable = false)
    private String optionC; // e.g., "5"

    @Column(nullable = false)
    private String optionD; // e.g., "6"

    /**
     * Which option is correct: "A", "B", "C", or "D".
     * We store the letter so it's easy to compare with the student's answer.
     */
    @Column(nullable = false)
    private String correctOption;

    /** Which quiz does this question belong to? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Quiz quiz;
}
