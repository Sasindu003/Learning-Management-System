package com.lms.lms.repository;

import com.lms.lms.entity.ClassAnnouncement;
import com.lms.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassAnnouncementRepository — handles database operations for teacher class
 * announcements.
 */
@Repository
public interface ClassAnnouncementRepository extends JpaRepository<ClassAnnouncement, Long> {

    /** Get announcements for a specific subject, newest first */
    List<ClassAnnouncement> findBySubjectOrderByCreatedAtDesc(Subject subject);

    /** Get all class announcements, newest first */
    List<ClassAnnouncement> findAllByOrderByCreatedAtDesc();
}
