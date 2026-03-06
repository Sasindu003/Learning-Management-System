package com.lms.lms.repository;

import com.lms.lms.model.Notification;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findTop10ByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndReadFalse(User user);

    long countByUserAndReadFalse(User user);
}
