package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final UserService userService;
    private final MessageService messageService;
    private final NotificationService notificationService;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("currentUser", user);
        if (user != null) {
            model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
            model.addAttribute("unreadMessages", messageService.getUnreadCount(user));
        }
    }

    @GetMapping("")
    public String inbox(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("messages", messageService.getInbox(user));
        model.addAttribute("tab", "inbox");
        return "messages/inbox";
    }

    @GetMapping("/sent")
    public String sent(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("messages", messageService.getSentMessages(user));
        model.addAttribute("tab", "sent");
        return "messages/inbox";
    }

    @GetMapping("/compose")
    public String compose(Model model) {
        model.addAttribute("users", userService.findAll());
        return "messages/compose";
    }

    @PostMapping("/send")
    public String send(@RequestParam("receiverId") Long receiverId,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content,
            Authentication auth, RedirectAttributes ra) {
        User sender = userService.findByUsername(auth.getName()).orElseThrow();
        User receiver = userService.findById(receiverId).orElseThrow();
        Message msg = Message.builder()
                .sender(sender).receiver(receiver)
                .subject(subject).content(content)
                .build();
        messageService.send(msg);
        notificationService.createNotification(receiver,
                "New message from " + sender.getFullName(),
                "/messages", Notification.NotificationType.MESSAGE);
        ra.addFlashAttribute("success", "Message sent!");
        return "redirect:/messages";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        Message msg = messageService.findById(id).orElseThrow();
        messageService.markAsRead(id);
        model.addAttribute("message", msg);
        return "messages/view";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        messageService.deleteForReceiver(id);
        ra.addFlashAttribute("success", "Message deleted!");
        return "redirect:/messages";
    }
}
