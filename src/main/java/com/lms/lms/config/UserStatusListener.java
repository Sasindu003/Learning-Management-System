package com.lms.lms.config;

import com.lms.lms.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusListener {

    private final UserStatusService userStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    // Track which username is associated with which sessionId for proper removal on disconnect
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();

        if (principal != null && principal.getName() != null) {
            String username = principal.getName();
            log.info("User connected: {} (Session: {})", username, sessionId);
            
            userStatusService.addUser(username);
            sessionUserMap.put(sessionId, username);

            // Broadcast status change to all subscribers
            Map<String, String> statusData = new java.util.HashMap<>();
            statusData.put("username", username);
            statusData.put("status", "ONLINE");
            
            messagingTemplate.convertAndSend("/topic/users/status", statusData);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = sessionUserMap.remove(sessionId);

        if (username != null) {
            log.info("User disconnected: {} (Session: {})", username, sessionId);
            
            // Check if user has other active sessions before marking offline (if they have multiple tabs)
            if (!sessionUserMap.containsValue(username)) {
                userStatusService.removeUser(username);

                // Broadcast status change
                Map<String, String> statusData = new java.util.HashMap<>();
                statusData.put("username", username);
                statusData.put("status", "OFFLINE");
                
                messagingTemplate.convertAndSend("/topic/users/status", statusData);
            }
        }
    }
}
