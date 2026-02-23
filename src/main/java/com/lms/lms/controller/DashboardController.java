package com.lms.lms.controller;

import com.lms.lms.entity.Announcement;
import com.lms.lms.entity.User;
import com.lms.lms.service.AnnouncementService;
import com.lms.lms.service.AssignmentService;
import com.lms.lms.service.ClassAnnouncementService;
import com.lms.lms.service.GradeService;
import com.lms.lms.service.LessonNoteService;
import com.lms.lms.service.QuizService;
import com.lms.lms.service.SubjectService;
import com.lms.lms.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * DashboardController — routes users to the correct dashboard based on their
 * role.
 *
 * After login, Spring Security sends everyone to /dashboard.
 * We check their role and redirect to the appropriate dashboard page.
 * Each role sees different content and has different actions available.
 */
@Controller
public class DashboardController {

    private final UserService userService;
    private final AnnouncementService announcementService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final LessonNoteService lessonNoteService;
    private final AssignmentService assignmentService;
    private final QuizService quizService;
    private final ClassAnnouncementService classAnnouncementService;

    public DashboardController(UserService userService,
            AnnouncementService announcementService,
            GradeService gradeService,
            SubjectService subjectService,
            LessonNoteService lessonNoteService,
            AssignmentService assignmentService,
            QuizService quizService,
            ClassAnnouncementService classAnnouncementService) {
        this.userService = userService;
        this.announcementService = announcementService;
        this.gradeService = gradeService;
        this.subjectService = subjectService;
        this.lessonNoteService = lessonNoteService;
        this.assignmentService = assignmentService;
        this.quizService = quizService;
        this.classAnnouncementService = classAnnouncementService;
    }

    /** Main dashboard router — redirects based on role */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_TEACHER" -> "redirect:/teacher/dashboard";
            case "ROLE_STUDENT" -> "redirect:/student/dashboard";
            default -> "redirect:/login";
        };
    }

    /** Admin Dashboard — shows stats and recent announcements */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        addCommonAttributes(authentication, model);
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalGrades", gradeService.getAllGrades().size());
        model.addAttribute("totalSubjects", subjectService.getAllSubjects().size());
        return "dashboard-admin";
    }

    /** Teacher Dashboard — shows content counts and announcements */
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(Authentication authentication, Model model) {
        addCommonAttributes(authentication, model);
        model.addAttribute("totalLessons", lessonNoteService.getAllLessonNotes().size());
        model.addAttribute("totalAssignments", assignmentService.getAllAssignments().size());
        model.addAttribute("totalQuizzes", quizService.getAllQuizzes().size());
        return "dashboard-teacher";
    }

    /** Student Dashboard — shows available content counts + class announcements */
    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        addCommonAttributes(authentication, model);
        model.addAttribute("totalLessons", lessonNoteService.getAllLessonNotes().size());
        model.addAttribute("totalAssignments", assignmentService.getAllAssignments().size());
        model.addAttribute("totalQuizzes", quizService.getAllQuizzes().size());
        // Class-specific announcements from teachers
        model.addAttribute("classAnnouncements", classAnnouncementService.getAllSorted());
        return "dashboard-student";
    }

    /** Helper — adds data that every dashboard needs */
    private void addCommonAttributes(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        model.addAttribute("user", user);
        List<Announcement> announcements = announcementService.getAllAnnouncementsSorted();
        model.addAttribute("announcements", announcements);
    }
}
