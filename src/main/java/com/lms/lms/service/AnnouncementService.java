package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.model.Announcement.TargetAudience;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public List<Announcement> findAll() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Announcement> findByTarget(TargetAudience target) {
        return announcementRepository.findByTargetAudience(target);
    }

    public List<Announcement> findRecent() {
        return announcementRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public Optional<Announcement> findById(Long id) {
        return announcementRepository.findById(id);
    }

    @Transactional
    public Announcement save(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }
}
