package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Submission entity — represents a student's answer to an assignment.
 *
 * WHY support both text and file submissions?
 * - Some assignments need written answers (text).
 * - Others might need file uploads (e.g., a PDF report).
 * - We store both options and let students choose.
 *
 * The relationship: One Assignment → Many Submissions (one per student).
 */
@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The student's text answer (optional — they might upload a file instead) */
    @Column(columnDefinition = "TEXT")
    private String answerText;

    /**
     * Path to uploaded file on the server (optional — they might type text instead)
     */
    private String filePath;

    /** Original filename for display/download */
    private String fileName;

    /** Which assignment is this submission for? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    /** Which student submitted this? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** Auto-set when the submission is created */
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }
}
