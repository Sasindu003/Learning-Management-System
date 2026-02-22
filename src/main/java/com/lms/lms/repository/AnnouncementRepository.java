package com.lms.lms.repository;

import com.lms.lms.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AnnouncementRepository — handles database operations for announcements.
 *
 * Custom query: findAllByOrderByCreatedAtDesc()
 * - Returns announcements sorted by newest first.
 * - This is what every dashboard uses to show the latest news.
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * Get all announcements, newest first.
     * Spring Data JPA reads the method name and generates:
     * SELECT * FROM announcements ORDER BY created_at DESC
     */
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
