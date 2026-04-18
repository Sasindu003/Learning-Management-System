package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final FileStorageService fileStorageService;


    @GetMapping("")
    public String viewProfile(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "profileImage", required = false) org.springframework.web.multipart.MultipartFile profileImage,
            Authentication auth, RedirectAttributes ra) {
        try {
            User user = userService.findByUsername(auth.getName()).orElseThrow();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            user.setBio(bio);

            if (profileImage != null && !profileImage.isEmpty()) {
                String imagePath = fileStorageService.store(profileImage, "profiles");
                user.setProfilePicture(imagePath);
            }

            userService.updateUser(user);
            ra.addFlashAttribute("success", "Profile updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String current,
            @RequestParam("newPassword") String newPass,
            @RequestParam("confirmPassword") String confirm,
            Authentication auth, RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        if (!userService.checkPassword(user, current)) {
            ra.addFlashAttribute("error", "Current password is incorrect!");
            return "redirect:/profile/edit";
        }
        if (!newPass.equals(confirm)) {
            ra.addFlashAttribute("error", "New passwords do not match!");
            return "redirect:/profile/edit";
        }
        if (newPass.length() < 6) {
            ra.addFlashAttribute("error", "Password must be at least 6 characters!");
            return "redirect:/profile/edit";
        }
        userService.updatePassword(user.getId(), newPass);
        ra.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/profile";
    }
}
