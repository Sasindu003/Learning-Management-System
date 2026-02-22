package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Announcement entity — global messages posted by admins.
 *
 * WHY a separate entity?
 * - Announcements show up on every user's dashboard regardless of role.
 * - Having them in their own table makes it easy to query "latest
 * announcements"
 * without mixing them with other data.
 */
@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // e.g., "School Closed on Friday"

    /**
     * Type of announcement for categorization.
     * GENERAL = regular news, EXAM = exam-related, HOLIDAY = holiday notices.
     * Defaults to "GENERAL" if not specified.
     */
    @Column(nullable = false)
    private String type = "GENERAL"; // "GENERAL", "EXAM", or "HOLIDAY"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // The full announcement message

    /** Which admin posted this? */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    /** Auto-set on creation — used to sort announcements (newest first) */
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
