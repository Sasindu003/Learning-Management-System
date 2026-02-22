package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Grade entity — represents a school grade level (e.g., "Grade 10", "Grade
 * 11").
 *
 * WHY a separate entity?
 * - Grades group subjects together. A Grade has many Subjects.
 * - Admins can create/delete grades from the admin panel.
 * - Keeping it as its own table means we can easily list, filter, and manage
 * grades.
 */
@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Each grade name must be unique
    private String name; // e.g., "Grade 10", "Grade 11"
}
