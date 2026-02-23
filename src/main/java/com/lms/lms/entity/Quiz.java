package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz entity — represents a quiz created by a teacher for a specific subject.
 * (Updated to trigger re-index)
 *
 * A quiz contains multiple QuizQuestion objects.
 * We use @ToString.Exclude and @EqualsAndHashCode.Exclude on the 'questions'
 * list
 * to prevent infinite loops when Lombok generates toString/equals/hashCode.
 *
 * WHY? Because QuizQuestion has a reference back to Quiz (bidirectional
 * relationship).
 * Without these excludes, calling quiz.toString() would call
 * question.toString(),
 * which would call quiz.toString() again → infinite loop → StackOverflowError!
 */
@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "Chapter 5 Quiz"

    /** Which subject is this quiz for? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /** Which teacher created this quiz? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * The questions in this quiz.
     * CascadeType.ALL: When we save/delete a quiz, its questions are also
     * saved/deleted.
     * orphanRemoval: If a question is removed from this list, it's deleted from the
     * DB.
     *
     * @ToString.Exclude prevents infinite toString() loops (Quiz ↔ QuizQuestion).
     */
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<QuizQuestion> questions = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
