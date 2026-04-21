package com.lms.lms.repository;

import com.lms.lms.model.User;
import com.lms.lms.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserOrderByLastSeenAtDesc(User user);
    Optional<UserSession> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
