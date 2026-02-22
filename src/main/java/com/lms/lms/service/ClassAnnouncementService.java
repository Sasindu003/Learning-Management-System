package com.lms.lms.service;

import com.lms.lms.entity.ClassAnnouncement;
import com.lms.lms.entity.Subject;
import com.lms.lms.repository.ClassAnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ClassAnnouncementService — business logic for teacher class announcements.
 */
@Service
public class ClassAnnouncementService {

    private final ClassAnnouncementRepository classAnnouncementRepository;

    public ClassAnnouncementService(ClassAnnouncementRepository classAnnouncementRepository) {
        this.classAnnouncementRepository = classAnnouncementRepository;
    }

    /** Get all class announcements, newest first */
    public List<ClassAnnouncement> getAllSorted() {
        return classAnnouncementRepository.findAllByOrderByCreatedAtDesc();
    }

    /** Get announcements for a specific subject/class */
    public List<ClassAnnouncement> getBySubject(Subject subject) {
        return classAnnouncementRepository.findBySubjectOrderByCreatedAtDesc(subject);
    }

    public Optional<ClassAnnouncement> findById(Long id) {
        return classAnnouncementRepository.findById(id);
    }

    public ClassAnnouncement save(ClassAnnouncement announcement) {
        return classAnnouncementRepository.save(announcement);
    }

    public void delete(Long id) {
        classAnnouncementRepository.deleteById(id);
    }
}
