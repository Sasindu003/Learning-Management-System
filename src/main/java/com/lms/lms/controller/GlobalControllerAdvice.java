package com.lms.lms.controller;

import com.lms.lms.model.User;
import com.lms.lms.service.MessageService;
import com.lms.lms.service.NotificationService;
import com.lms.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            userService.findByUsername(auth.getName()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
                model.addAttribute("unreadMessages", messageService.getUnreadCount(user));
            });
        }
    }
}
