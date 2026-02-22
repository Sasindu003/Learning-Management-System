package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ClassAnnouncement entity — messages posted by teachers to specific classes
 * (subjects).
 *
 * WHY a separate entity from Announcement?
 * - Announcements are GLOBAL (posted by admins, visible to everyone).
 * - ClassAnnouncements are SCOPED to a specific subject (posted by teachers).
 * - Keeping them separate makes queries simpler and avoids messy null checks.
 */
@Entity
@Table(name = "class_announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** Which subject/class is this announcement for? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /** Which teacher posted this? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
