package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Subject entity — represents a subject within a grade (e.g., "Math" in "Grade
 * 10").
 *
 * WHY a ManyToOne relationship with Grade?
 * - Each subject belongs to exactly one grade.
 * - A grade can have many subjects (e.g., Grade 10 → Math, Science, English).
 * - This lets us filter subjects by grade when teachers upload lessons.
 */
@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "Mathematics", "Science"

    /**
     * @ManyToOne: Many subjects can belong to one grade.
     * @JoinColumn: Creates a "grade_id" foreign key column in the subjects table.
     *              FetchType.LAZY: Don't load the Grade object until we actually
     *              need it (performance).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;
}
