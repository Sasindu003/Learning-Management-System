package com.lms.lms.service;

import com.lms.lms.model.User;
import com.lms.lms.model.UserSession;
import com.lms.lms.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository sessionRepository;
    private final SessionRegistry sessionRegistry;

    @Transactional
    public void createOrUpdateSession(User user, String sessionId, String ipAddress, String userAgent) {
        UserSession session = sessionRepository.findBySessionId(sessionId)
                .orElse(UserSession.builder().sessionId(sessionId).user(user).build());
        
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setLastSeenAt(LocalDateTime.now());
        
        sessionRepository.save(session);
    }

    public List<UserSession> getActiveSessionsForUser(User user) {
        return sessionRepository.findByUserOrderByLastSeenAtDesc(user);
    }

    @Transactional
    public void revokeSession(String sessionId) {
        SessionInformation info = sessionRegistry.getSessionInformation(sessionId);
        if (info != null) {
            info.expireNow();
        }
        sessionRepository.deleteBySessionId(sessionId);
    }
    
    @Transactional
    public void cleanExpiredSessions() {
        // This could be called by a logout listener or a scheduled task
        // For simplicity, we just handle revocation
    }
}
