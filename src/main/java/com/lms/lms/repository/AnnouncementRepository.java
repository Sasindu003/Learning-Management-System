package com.lms.lms.repository;

import com.lms.lms.model.Announcement;
import com.lms.lms.model.Announcement.TargetAudience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByCreatedAtDesc();

    @Query("SELECT a FROM Announcement a WHERE a.targetAudience = :target OR a.targetAudience = 'ALL' ORDER BY a.pinned DESC, a.createdAt DESC")
    List<Announcement> findByTargetAudience(@Param("target") TargetAudience target);

    List<Announcement> findTop5ByOrderByCreatedAtDesc();
}
