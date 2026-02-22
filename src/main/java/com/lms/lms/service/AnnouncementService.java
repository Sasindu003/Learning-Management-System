package com.lms.lms.service;

import com.lms.lms.entity.Announcement;
import com.lms.lms.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * AnnouncementService — business logic for announcements.
 *
 * Announcements are sorted newest-first by default so dashboards
 * always show the most recent news at the top.
 */
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * Get all announcements, newest first.
     * This is the main method used by all dashboards.
     */
    public List<Announcement> getAllAnnouncementsSorted() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    /** Find an announcement by ID */
    public Optional<Announcement> findById(Long id) {
        return announcementRepository.findById(id);
    }

    /** Save a new announcement or update an existing one */
    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    /** Delete an announcement by ID */
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}
