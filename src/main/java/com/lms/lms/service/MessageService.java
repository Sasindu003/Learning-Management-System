package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private void broadcastCount(User user) {
        long count = getUnreadCount(user);
        messagingTemplate.convertAndSendToUser(user.getUsername(), "/topic/messages/unread-count", count);
    }

    public List<Message> getInbox(User user) {
        return messageRepository.findByReceiverAndDeletedByReceiverFalseOrderBySentAtDesc(user);
    }

    public List<Message> getSentMessages(User user) {
        return messageRepository.findBySenderAndDeletedBySenderFalseOrderBySentAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return messageRepository.countByReceiverAndReadFalseAndDeletedByReceiverFalse(user);
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public Message send(Message message) {
        Message saved = messageRepository.save(message);
        broadcastCount(message.getReceiver());
        return saved;
    }

    @Transactional
    public void markAsRead(Long id) {
        messageRepository.findById(id).ifPresent(m -> {
            m.setRead(true);
            messageRepository.save(m);
            broadcastCount(m.getReceiver());
        });
    }

    @Transactional
    public void deleteForReceiver(Long id) {
        messageRepository.findById(id).ifPresent(m -> {
            m.setDeletedByReceiver(true);
            messageRepository.save(m);
        });
    }
}
