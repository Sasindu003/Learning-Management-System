package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final ExamService examService;
    private final StudentGradeService studentGradeService;
    private final FileStorageService fileStorageService;
    private final AttendanceService attendanceService;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final AnnouncementService announcementService;

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
    public String dashboard(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null ? courseService.findByGrade(student.getGrade())
                : Collections.emptyList();
        model.addAttribute("courses", courses);
        model.addAttribute("recentAnnouncements", announcementService.findRecent());

        // Pending assignments
        List<Assignment> pending = new ArrayList<>();
        for (Course c : courses) {
            for (Assignment a : assignmentService.findByCourse(c)) {
                if (!a.isOverdue() && !assignmentService.hasSubmitted(a, student)) {
                    pending.add(a);
                }
            }
        }
        model.addAttribute("pendingAssignments", pending);

        // Available quizzes
        List<Exam> quizzes = examService.findPublishedByCourses(courses);
        List<Exam> available = new ArrayList<>();
        for (Exam e : quizzes) {
            if (!examService.hasAttempted(e, student)) {
                available.add(e);
            }
        }
        model.addAttribute("availableQuizzes", available);
        return "student/dashboard";
    }

    // === Courses ===
    @GetMapping("/courses")
    public String courses(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null ? courseService.findByGrade(student.getGrade())
                : Collections.emptyList();
        model.addAttribute("courses", courses);
        return "student/courses";
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable("id") Long id, Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(id).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("materials", courseService.getMaterials(course));
        model.addAttribute("assignments", assignmentService.findByCourse(course));
        model.addAttribute("exams", examService.findByCourse(course));
        // Mark which assignments already submitted
        Map<Long, Boolean> submitted = new HashMap<>();
        for (Assignment a : assignmentService.findByCourse(course)) {
            submitted.put(a.getId(), assignmentService.hasSubmitted(a, student));
        }
        model.addAttribute("submitted", submitted);
        return "student/course-detail";
    }

    // === Assignments ===
    @GetMapping("/assignments")
    public String assignments(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null ? courseService.findByGrade(student.getGrade())
                : Collections.emptyList();
        List<Assignment> all = assignmentService.findByCourses(courses);
        Map<Long, Boolean> submitted = new HashMap<>();
        Map<Long, AssignmentSubmission> submissions = new HashMap<>();
        for (Assignment a : all) {
            submitted.put(a.getId(), assignmentService.hasSubmitted(a, student));
            assignmentService.getSubmission(a, student).ifPresent(s -> submissions.put(a.getId(), s));
        }
        model.addAttribute("assignments", all);
        model.addAttribute("submitted", submitted);
        model.addAttribute("submissions", submissions);
        return "student/assignments";
    }

    @GetMapping("/assignments/{id}/submit")
    public String submitForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("assignment", assignmentService.findById(id).orElseThrow());
        return "student/submit-assignment";
    }

    @PostMapping("/assignments/{id}/submit")
    public String submitAssignment(@PathVariable("id") Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "comments", required = false) String comments,
            Authentication auth, RedirectAttributes ra) {
        try {
            User student = userService.findByUsername(auth.getName()).orElseThrow();
            Assignment assignment = assignmentService.findById(id).orElseThrow();

            AssignmentSubmission sub = AssignmentSubmission.builder()
                    .assignment(assignment).student(student).comments(comments)
                    .build();
            if (file != null && !file.isEmpty()) {
                String path = fileStorageService.store(file, "submissions");
                sub.setFilePath(path);
                sub.setFileName(file.getOriginalFilename());
            }
            assignmentService.submitAssignment(sub);
            ra.addFlashAttribute("success", "Assignment submitted!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/student/assignments";
    }

    // === Quizzes / Exams ===
    @GetMapping("/quizzes")
    public String quizzes(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null ? courseService.findByGrade(student.getGrade())
                : Collections.emptyList();
        List<Exam> exams = examService.findPublishedByCourses(courses);
        Map<Long, Boolean> attempted = new HashMap<>();
        Map<Long, ExamAttempt> attempts = new HashMap<>();
        for (Exam e : exams) {
            attempted.put(e.getId(), examService.hasAttempted(e, student));
            examService.getAttempt(e, student).ifPresent(a -> attempts.put(e.getId(), a));
        }
        model.addAttribute("exams", exams);
        model.addAttribute("attempted", attempted);
        model.addAttribute("attempts", attempts);
        return "student/quizzes";
    }

    @GetMapping("/quizzes/{id}/take")
    public String takeQuiz(@PathVariable("id") Long id, Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        Exam exam = examService.findById(id).orElseThrow();

        if (examService.hasAttempted(exam, student)) {
            return "redirect:/student/quizzes";
        }

        ExamAttempt attempt = examService.startAttempt(exam, student);
        model.addAttribute("exam", exam);
        model.addAttribute("attempt", attempt);
        model.addAttribute("questions", examService.getQuestions(exam));
        return "student/take-quiz";
    }

    @PostMapping("/quizzes/{attemptId}/submit")
    public String submitQuiz(@PathVariable("attemptId") Long attemptId,
            @RequestParam Map<String, String> params,
            RedirectAttributes ra) {
        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("answer_")) {
                Long qId = Long.parseLong(entry.getKey().replace("answer_", ""));
                answers.put(qId, entry.getValue());
            }
        }
        ExamAttempt attempt = examService.submitAttempt(attemptId, answers);
        ra.addFlashAttribute("success",
                "Quiz submitted! Score: " + attempt.getScore() + "/" + attempt.getTotalMarks()
                        + " (" + String.format("%.1f", attempt.getPercentage()) + "%)");
        return "redirect:/student/quizzes";
    }

    // === Grades / Report Card ===
    @GetMapping("/grades")
    public String grades(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("grades", studentGradeService.findByStudent(student));
        model.addAttribute("gpa", studentGradeService.calculateGPA(student));
        return "student/grades";
    }
}
