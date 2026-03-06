package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @GetMapping("/notifications")
    public String notifications(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("currentUser", user);
        model.addAttribute("notifications", notificationService.getAllNotifications(user));
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
        model.addAttribute("unreadMessages", messageService.getUnreadCount(user));
        return "notifications";
    }

    @GetMapping("/notifications/read/{id}")
    public String markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @GetMapping("/notifications/read-all")
    public String markAllAsRead(Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }

    @GetMapping("/api/notifications/count")
    @ResponseBody
    public Map<String, Long> getNotificationCount(Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Map<String, Long> counts = new HashMap<>();
        counts.put("notifications", notificationService.getUnreadCount(user));
        counts.put("messages", messageService.getUnreadCount(user));
        return counts;
    }
}
