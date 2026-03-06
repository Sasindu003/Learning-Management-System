package com.lms.lms.controller;

import com.lms.lms.model.User;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        switch (user.getRole()) {
            case ADMIN:
                return "redirect:/admin/dashboard";
            case TEACHER:
                return "redirect:/teacher/dashboard";
            case STUDENT:
                return "redirect:/student/dashboard";
            default:
                return "redirect:/login";
        }
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
