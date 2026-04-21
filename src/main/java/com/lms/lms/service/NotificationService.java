package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.model.Notification.NotificationType;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private void broadcastCount(User user) {
        long count = getUnreadCount(user);
        messagingTemplate.convertAndSendToUser(user.getUsername(), "/topic/notifications/unread-count", count);
    }

    public List<Notification> getRecentNotifications(User user) {
        return notificationRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public Notification createNotification(User user, String message, String link, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .link(link)
                .type(type)
                .build();
        Notification saved = notificationRepository.save(notification);
        broadcastCount(user);
        return saved;
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
            broadcastCount(n.getUser());
        });
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        broadcastCount(user);
    }
}
