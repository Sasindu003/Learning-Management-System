package com.lms.lms.component;

import com.lms.lms.model.LoginLog;
import com.lms.lms.model.User;
import com.lms.lms.service.LoginLogService;
import com.lms.lms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class LoginListener {

    private final LoginLogService loginLogService;
    private final UserService userService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        User user = userService.findByUsername(username).orElse(null);
        
        LoginLog log = LoginLog.builder()
                .user(user)
                .username(username)
                .success(true)
                .ipAddress(getClientIp())
                .userAgent(getUserAgent())
                .build();
        
        loginLogService.save(log);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        User user = userService.findByUsername(username).orElse(null);

        LoginLog log = LoginLog.builder()
                .user(user)
                .username(username)
                .success(false)
                .failureReason(event.getException().getMessage())
                .ipAddress(getClientIp())
                .userAgent(getUserAgent())
                .build();

        loginLogService.save(log);
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0];
            }
            return request.getRemoteAddr();
        }
        return "Unknown";
    }

    private String getUserAgent() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("User-Agent");
        }
        return "Unknown";
    }
}
