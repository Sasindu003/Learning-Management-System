package com.lms.lms.controller;

import com.lms.lms.entity.*;
import com.lms.lms.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AdminController — handles all admin-only pages.
 *
 * This controller manages:
 * - Grades: Create and delete grade levels (e.g., "Grade 10")
 * - Subjects: Create and delete subjects within grades
 * - Announcements: Post and delete global announcements
 * - Users: Create and delete teacher/student accounts
 *
 * All URLs start with /admin/ — Spring Security ensures only ADMIN users can
 * access these.
 */
@Controller
@RequestMapping("/admin") // All methods in this controller start with /admin
public class AdminController {

    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final AnnouncementService announcementService;
    private final UserService userService;

    public AdminController(GradeService gradeService,
            SubjectService subjectService,
            AnnouncementService announcementService,
            UserService userService) {
        this.gradeService = gradeService;
        this.subjectService = subjectService;
        this.announcementService = announcementService;
        this.userService = userService;
    }

    // ==================== GRADES ====================

    /**
     * Show the grades management page.
     * Displays a form to add new grades and a list of existing ones.
     */
    @GetMapping("/grades")
    public String gradesPage(Model model) {
        model.addAttribute("grades", gradeService.getAllGrades());
        model.addAttribute("newGrade", new Grade()); // Empty object for the form
        return "admin/grades"; // Renders templates/admin/grades.html
    }

    /**
     * Handle the "Add Grade" form submission.
     *
     * @ModelAttribute maps the form fields to a Grade object automatically.
     *                 RedirectAttributes lets us show a success message after
     *                 redirecting.
     */
    @PostMapping("/grades")
    public String addGrade(@ModelAttribute Grade grade, RedirectAttributes redirectAttributes) {
        gradeService.saveGrade(grade);
        redirectAttributes.addFlashAttribute("success", "Grade added successfully!");
        return "redirect:/admin/grades"; // Redirect to prevent form resubmission on refresh
    }

    /** Delete a grade by ID */
    @PostMapping("/grades/delete/{id}")
    public String deleteGrade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gradeService.deleteGrade(id);
        redirectAttributes.addFlashAttribute("success", "Grade deleted successfully!");
        return "redirect:/admin/grades";
    }

    // ==================== SUBJECTS ====================

    /**
     * Show the subjects management page.
     * Also loads all grades for the "select grade" dropdown in the form.
     */
    @GetMapping("/subjects")
    public String subjectsPage(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("grades", gradeService.getAllGrades()); // For the dropdown
        model.addAttribute("newSubject", new Subject());
        return "admin/subjects";
    }

    /**
     * Handle "Add Subject" form submission.
     *
     * @RequestParam gradeId: the grade ID selected from the dropdown.
     *               We load the Grade entity and set it on the Subject before
     *               saving.
     */
    @PostMapping("/subjects")
    public String addSubject(@RequestParam String name,
            @RequestParam Long gradeId,
            RedirectAttributes redirectAttributes) {
        // Look up the grade — if it doesn't exist, redirect with an error
        Grade grade = gradeService.findById(gradeId).orElse(null);
        if (grade == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid grade selected.");
            return "redirect:/admin/subjects";
        }

        Subject subject = new Subject();
        subject.setName(name);
        subject.setGrade(grade);
        subjectService.saveSubject(subject);

        redirectAttributes.addFlashAttribute("success", "Subject added successfully!");
        return "redirect:/admin/subjects";
    }

    /** Delete a subject by ID */
    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        subjectService.deleteSubject(id);
        redirectAttributes.addFlashAttribute("success", "Subject deleted successfully!");
        return "redirect:/admin/subjects";
    }

    // ==================== ANNOUNCEMENTS ====================

    /** Show the announcements management page */
    @GetMapping("/announcements")
    public String announcementsPage(Model model) {
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        model.addAttribute("newAnnouncement", new Announcement());
        return "admin/announcements";
    }

    /**
     * Handle "Post Announcement" form submission.
     * Sets the current admin as the poster. Includes notification type.
     */
    @PostMapping("/announcements")
    public String addAnnouncement(@RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "GENERAL") String type,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        // Get the current admin user from the database
        User admin = userService.findByUsername(authentication.getName()).orElse(null);

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setType(type); // GENERAL, EXAM, or HOLIDAY
        announcement.setPostedBy(admin);
        announcementService.saveAnnouncement(announcement);

        redirectAttributes.addFlashAttribute("success", "Announcement posted successfully!");
        return "redirect:/admin/announcements";
    }

    /** Delete an announcement by ID */
    @PostMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        announcementService.deleteAnnouncement(id);
        redirectAttributes.addFlashAttribute("success", "Announcement deleted successfully!");
        return "redirect:/admin/announcements";
    }

    // ==================== USER MANAGEMENT ====================

    /** Show the user management page */
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    /**
     * Handle "Create User" form submission.
     * Admin can create TEACHER or STUDENT accounts.
     */
    @PostMapping("/users")
    public String createUser(@RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        // Check if username is already taken
        if (userService.usernameExists(username)) {
            redirectAttributes.addFlashAttribute("error", "Username '" + username + "' is already taken.");
            return "redirect:/admin/users";
        }

        // Convert the role string to our Role enum
        Role userRole = Role.valueOf(role.toUpperCase());
        userService.createUser(username, password, fullName, userRole);

        redirectAttributes.addFlashAttribute("success", "User '" + fullName + "' created successfully!");
        return "redirect:/admin/users";
    }

    /**
     * Toggle a user's active/suspended status.
     * Suspended users cannot log in — they get blocked by Spring Security.
     */
    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
        return "redirect:/admin/users";
    }

    /** Delete a user by ID */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/users";
    }
}
