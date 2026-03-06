package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final CourseService courseService;
    private final AnnouncementService announcementService;
    private final EventService eventService;
    private final AcademicTermService termService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("currentUser", user);
        if (user != null) {
            model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
            model.addAttribute("unreadMessages", messageService.getUnreadCount(user));
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", userService.countByRole(Role.STUDENT));
        model.addAttribute("teacherCount", userService.countByRole(Role.TEACHER));
        model.addAttribute("courseCount", courseService.findAll().size());
        model.addAttribute("gradeCount", gradeService.findAll().size());
        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        model.addAttribute("upcomingEvents", eventService.findUpcoming());
        return "admin/dashboard";
    }

    // === User Management ===
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("teachers", userService.findByRole(Role.TEACHER));
        model.addAttribute("students", userService.findByRole(Role.STUDENT));
        model.addAttribute("admins", userService.findByRole(Role.ADMIN));
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("roles", User.Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user,
            @RequestParam(value = "gradeId", required = false) Long gradeId,
            @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
            RedirectAttributes ra) {
        if (user.getId() == null && userService.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already exists!");
            return "redirect:/admin/users/new";
        }

        if (gradeId != null) {
            user.setGrade(gradeService.findById(gradeId).orElse(null));
        }
        if (subjectIds != null) {
            Set<Subject> subjects = new HashSet<>();
            for (Long sid : subjectIds) {
                subjectService.findById(sid).ifPresent(subjects::add);
            }
            user.setSubjects(subjects);
        }

        if (user.getId() == null) {
            userService.createUser(user);
            ra.addFlashAttribute("success", "User created successfully!");
        } else {
            User existing = userService.findById(user.getId()).orElseThrow();
            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            existing.setPhone(user.getPhone());
            existing.setRole(user.getRole());
            existing.setGrade(user.getGrade());
            existing.setSubjects(user.getSubjects());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userService.updatePassword(user.getId(), user.getPassword());
            }
            userService.updateUser(existing);
            ra.addFlashAttribute("success", "User updated successfully!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id).orElseThrow());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("roles", User.Role.values());
        return "admin/user-form";
    }

    @GetMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.toggleUserStatus(id);
        ra.addFlashAttribute("success", "User status updated!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted!");
        return "redirect:/admin/users";
    }

    // === Grade Management ===
    @GetMapping("/grades")
    public String grades(Model model) {
        model.addAttribute("grades", gradeService.findAll());
        return "admin/grades";
    }

    @PostMapping("/grades/save")
    public String saveGrade(@RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "id", required = false) Long id,
            RedirectAttributes ra) {
        Grade grade = id != null ? gradeService.findById(id).orElse(new Grade()) : new Grade();
        grade.setName(name);
        grade.setDescription(description);
        gradeService.save(grade);
        ra.addFlashAttribute("success", "Grade saved successfully!");
        return "redirect:/admin/grades";
    }

    @GetMapping("/grades/delete/{id}")
    public String deleteGrade(@PathVariable("id") Long id, RedirectAttributes ra) {
        gradeService.delete(id);
        ra.addFlashAttribute("success", "Grade deleted!");
        return "redirect:/admin/grades";
    }

    // === Subject Management ===
    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "admin/subjects";
    }

    @PostMapping("/subjects/save")
    public String saveSubject(@RequestParam("name") String name,
            @RequestParam("code") String code,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "id", required = false) Long id,
            RedirectAttributes ra) {
        Subject subject = id != null ? subjectService.findById(id).orElse(new Subject()) : new Subject();
        subject.setName(name);
        subject.setCode(code);
        subject.setDescription(description);
        subjectService.save(subject);
        ra.addFlashAttribute("success", "Subject saved successfully!");
        return "redirect:/admin/subjects";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id, RedirectAttributes ra) {
        subjectService.delete(id);
        ra.addFlashAttribute("success", "Subject deleted!");
        return "redirect:/admin/subjects";
    }

    // === Announcements ===
    @GetMapping("/announcements")
    public String announcements(Model model) {
        model.addAttribute("announcements", announcementService.findAll());
        return "admin/announcements";
    }

    @PostMapping("/announcements/save")
    public String saveAnnouncement(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("targetAudience") Announcement.TargetAudience target,
            @RequestParam(value = "pinned", defaultValue = "false") boolean pinned,
            Authentication auth,
            RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Announcement a = Announcement.builder()
                .title(title).content(content).targetAudience(target).pinned(pinned).author(user)
                .build();
        announcementService.save(a);
        ra.addFlashAttribute("success", "Announcement published!");
        return "redirect:/admin/announcements";
    }

    @GetMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable("id") Long id, RedirectAttributes ra) {
        announcementService.delete(id);
        ra.addFlashAttribute("success", "Announcement deleted!");
        return "redirect:/admin/announcements";
    }

    // === Events ===
    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.findAll());
        return "admin/events";
    }

    @PostMapping("/events/save")
    public String saveEvent(@RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("eventDate") java.time.LocalDate eventDate,
            @RequestParam(value = "eventTime", required = false) String eventTime,
            @RequestParam("type") Event.EventType type,
            Authentication auth,
            RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Event e = Event.builder()
                .title(title).description(description).eventDate(eventDate)
                .eventTime(eventTime).type(type).createdBy(user)
                .build();
        eventService.save(e);
        ra.addFlashAttribute("success", "Event created!");
        return "redirect:/admin/events";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id, RedirectAttributes ra) {
        eventService.delete(id);
        ra.addFlashAttribute("success", "Event deleted!");
        return "redirect:/admin/events";
    }

    // === Academic Terms ===
    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("terms", termService.findAll());
        return "admin/terms";
    }

    @PostMapping("/terms/save")
    public String saveTerm(@RequestParam("name") String name,
            @RequestParam("startDate") java.time.LocalDate startDate,
            @RequestParam("endDate") java.time.LocalDate endDate,
            @RequestParam(value = "active", defaultValue = "false") boolean active,
            RedirectAttributes ra) {
        AcademicTerm term = AcademicTerm.builder()
                .name(name).startDate(startDate).endDate(endDate).active(active)
                .build();
        termService.save(term);
        ra.addFlashAttribute("success", "Term saved!");
        return "redirect:/admin/terms";
    }

    @GetMapping("/terms/delete/{id}")
    public String deleteTerm(@PathVariable("id") Long id, RedirectAttributes ra) {
        termService.delete(id);
        ra.addFlashAttribute("success", "Term deleted!");
        return "redirect:/admin/terms";
    }

    // === Dev Quick Login ===
    @GetMapping("/users/login-as/{id}")
    public String loginAs(@PathVariable("id") Long id, RedirectAttributes ra) {
        User target = userService.findById(id).orElse(null);
        if (target == null) {
            ra.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/users";
        }
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + target.getRole().name()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                target.getUsername(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/dashboard";
    }
}
