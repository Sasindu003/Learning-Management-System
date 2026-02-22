package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Assignment entity — a task created by a teacher for students to complete.
 *
 * WHY LocalDate for dueDate but LocalDateTime for createdAt?
 * - dueDate only needs a date (e.g., "Feb 28, 2026") — no time precision
 * needed.
 * - createdAt needs the full timestamp to sort assignments by creation time.
 */
@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "Homework: Chapter 5 Problems"

    @Column(columnDefinition = "TEXT") // TEXT allows longer descriptions than VARCHAR(255)
    private String description; // Detailed instructions for the assignment

    @Column(nullable = false)
    private LocalDate dueDate; // When the assignment is due

    /** Which subject is this assignment for? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /** Which teacher created this assignment? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /** Auto-set when the assignment is first saved */
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
