package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * LessonNote entity — represents a file (PDF, DOCX, etc.) uploaded by a
 * teacher.
 *
 * WHY store filePath instead of the actual file bytes?
 * - Storing large files (BLOBs) in the database is slow and expensive.
 * - We save the file to the local filesystem and only store the path in the DB.
 * - This makes it easy to serve files via a URL or move to cloud storage later.
 */
@Entity
@Table(name = "lesson_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // A human-readable title like "Chapter 5 - Trigonometry"

    @Column(nullable = false)
    private String filePath; // Path to the uploaded file on the server

    @Column(nullable = false)
    private String fileName; // Original file name for display/download purposes

    /**
     * Which subject does this lesson belong to?
     * This lets students filter lessons by subject.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /**
     * Which teacher uploaded this note?
     * Useful for tracking who created what content.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    /** When the note was uploaded — auto-set before saving */
    private LocalDateTime uploadedAt;

    /**
     * @PrePersist runs automatically before JPA saves a new entity to the DB.
     *             This ensures uploadedAt is always set without us having to
     *             remember to do it.
     */
    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
