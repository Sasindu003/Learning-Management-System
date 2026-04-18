package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import com.lms.lms.dto.UnifiedGradeDTO;
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
    private final TimetableService timetableService;


    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null ? courseService.findByGrade(student.getGrade())
                : Collections.emptyList();
        model.addAttribute("courses", courses); // All courses for stats

        // Pinned courses for the grid (limit to 4)
        List<Course> pinned = new ArrayList<>(student.getPinnedCourses());
        if (pinned.size() > 4) {
            pinned = pinned.subList(0, 4);
        }
        model.addAttribute("pinnedCourses", pinned);

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

        // Group courses by subject
        Map<String, List<Course>> coursesBySubject = new LinkedHashMap<>();
        for (Course course : courses) {
            String subjectName = course.getSubject().getName();
            coursesBySubject.computeIfAbsent(subjectName, k -> new ArrayList<>()).add(course);
        }

        // Per-course status stats for filtering
        Map<Long, Map<String, Integer>> courseStats = new HashMap<>();
        for (Course c : courses) {
            Map<String, Integer> stats = new HashMap<>();
            List<Assignment> assignments = assignmentService.findByCourse(c);
            int pendingAssignments = 0, completedAssignments = 0;
            for (Assignment a : assignments) {
                if (assignmentService.hasSubmitted(a, student)) {
                    completedAssignments++;
                } else if (!a.isOverdue()) {
                    pendingAssignments++;
                }
            }
            List<Exam> exams = examService.findByCourse(c);
            int pendingQuizzes = 0, completedQuizzes = 0;
            for (Exam e : exams) {
                if (!e.isPublished()) continue;
                if (examService.hasAttempted(e, student)) {
                    completedQuizzes++;
                } else {
                    pendingQuizzes++;
                }
            }
            stats.put("pendingAssignments", pendingAssignments);
            stats.put("completedAssignments", completedAssignments);
            stats.put("pendingQuizzes", pendingQuizzes);
            stats.put("completedQuizzes", completedQuizzes);
            courseStats.put(c.getId(), stats);
        }

        model.addAttribute("coursesBySubject", coursesBySubject);
        model.addAttribute("courseStats", courseStats);
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

            // Check if already submitted, update existing or create new
            AssignmentSubmission sub = assignmentService.getSubmission(assignment, student)
                    .orElse(AssignmentSubmission.builder()
                            .assignment(assignment)
                            .student(student)
                            .build());

            if (file != null && !file.isEmpty()) {
                String path = fileStorageService.store(file, "submissions");
                sub.setFilePath(path);
                sub.setFileName(file.getOriginalFilename());
            }
            if (comments != null) {
                sub.setComments(comments);
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
    public String grades(Model model, Authentication auth,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "course", required = false) String course,
            @RequestParam(value = "type", required = false) String type) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        
        List<UnifiedGradeDTO> allGrades = new ArrayList<>();

        // 1. Fetch Manual Grades
        List<StudentGrade> manualGrades = studentGradeService.findByStudent(student);
        for (StudentGrade g : manualGrades) {
            allGrades.add(UnifiedGradeDTO.builder()
                .type("Manual")
                .subjectName(g.getSubject().getName())
                .courseTitle(g.getCourse() != null ? g.getCourse().getTitle() : "N/A")
                .itemName(g.getExamName())
                .marks(g.getMarks())
                .maxMarks(g.getMaxMarks())
                .percentage(g.getPercentage())
                .letterGrade(g.getLetterGrade())
                .term(g.getTerm())
                .remarks(g.getRemarks())
                .date(g.getRecordedAt())
                .build());
        }

        // 2. Fetch Assignments
        List<AssignmentSubmission> submissions = assignmentService.getStudentSubmissions(student);
        for (AssignmentSubmission s : submissions) {
            String subTerm = "Term - " + s.getSubmittedAt().getYear() + " " + s.getSubmittedAt().getMonth().name();
            double pct = s.getAssignment().getMaxMarks() > 0 && s.getMarks() != null 
                    ? ((double) s.getMarks() / s.getAssignment().getMaxMarks()) * 100 : 0.0;
            allGrades.add(UnifiedGradeDTO.builder()
                .type("Assignment")
                .subjectName(s.getAssignment().getCourse().getSubject().getName())
                .courseTitle(s.getAssignment().getCourse().getTitle())
                .itemName(s.getAssignment().getTitle())
                .marks(s.getMarks() != null ? s.getMarks() : 0.0)
                .maxMarks(s.getAssignment().getMaxMarks())
                .percentage(pct)
                .letterGrade(UnifiedGradeDTO.calculateLetterGrade(pct))
                .term(subTerm)
                .remarks(s.getFeedback())
                .date(s.getSubmittedAt())
                .build());
        }

        // 3. Fetch Quizzes (Exams)
        List<ExamAttempt> attempts = examService.getStudentAttempts(student);
        for (ExamAttempt a : attempts) {
            if (!a.isCompleted()) continue;
            String examTerm = "Term - " + a.getEndTime().getYear() + " " + a.getEndTime().getMonth().name();
            allGrades.add(UnifiedGradeDTO.builder()
                .type("Quiz")
                .subjectName(a.getExam().getCourse().getSubject().getName())
                .courseTitle(a.getExam().getCourse().getTitle())
                .itemName(a.getExam().getTitle())
                .marks(a.getScore())
                .maxMarks(a.getTotalMarks())
                .percentage(a.getPercentage())
                .letterGrade(UnifiedGradeDTO.calculateLetterGrade(a.getPercentage()))
                .term(examTerm)
                .remarks("")
                .date(a.getEndTime())
                .build());
        }

        // Extract distinct filter options
        Set<String> subjects = new TreeSet<>();
        Set<String> courses = new TreeSet<>();
        Set<String> terms = new TreeSet<>();
        for (UnifiedGradeDTO dto : allGrades) {
            if (dto.getSubjectName() != null) subjects.add(dto.getSubjectName());
            if (dto.getCourseTitle() != null) courses.add(dto.getCourseTitle());
            if (dto.getTerm() != null) terms.add(dto.getTerm());
        }

        // Apply filters
        List<UnifiedGradeDTO> filteredGrades = new ArrayList<>();
        double totalPct = 0;
        int countWithPct = 0;

        for (UnifiedGradeDTO dto : allGrades) {
            boolean matchTerm = (term == null || term.isEmpty() || term.equals(dto.getTerm()));
            boolean matchSubject = (subject == null || subject.isEmpty() || subject.equals(dto.getSubjectName()));
            boolean matchCourse = (course == null || course.isEmpty() || course.equals(dto.getCourseTitle()));
            boolean matchType = (type == null || type.isEmpty() || type.equals("All") || type.equals(dto.getType()));

            if (matchTerm && matchSubject && matchCourse && matchType) {
                filteredGrades.add(dto);
                // "marks" usually is 0 if not graded, or something. We can only average if we have maxMarks > 0
                if (dto.getMaxMarks() > 0) {
                    totalPct += dto.getPercentage();
                    countWithPct++;
                }
            }
        }
        
        filteredGrades.sort((a, b) -> {
            if (a.getDate() != null && b.getDate() != null) return b.getDate().compareTo(a.getDate());
            return 0; // fallback if dates are somehow null, though they shouldn't be
        });

        double overallAverage = countWithPct > 0 ? (totalPct / countWithPct) : 0.0;

        model.addAttribute("grades", filteredGrades);
        model.addAttribute("overallAverage", overallAverage);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedSubject", subject);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("selectedType", type != null ? type : "All");
        
        model.addAttribute("allSubjects", subjects);
        model.addAttribute("allCourses", courses);
        model.addAttribute("allTerms", terms);

        return "student/grades";
    }

    // === Timetable ===
    @GetMapping("/timetable")
    public String timetable(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        if (student.getGrade() != null) {
            model.addAttribute("timetable", timetableService.findByGrade(student.getGrade()));
        } else {
            model.addAttribute("timetable", Collections.emptyList());
        }
        return "student/timetable";
    }

    // === Attendance View ===
    @GetMapping("/attendance")
    public String attendance(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null ? courseService.findByGrade(student.getGrade())
                : Collections.emptyList();
        Map<String, Map<String, Long>> attendanceStats = new java.util.LinkedHashMap<>();
        for (Course c : courses) {
            attendanceStats.put(c.getTitle(), attendanceService.getStats(student, c));
        }
        model.addAttribute("attendanceStats", attendanceStats);
        return "student/attendance";
    }

    // === Announcements ===
    @GetMapping("/announcements")
    public String announcements(Model model) {
        model.addAttribute("announcements",
                announcementService.findByTarget(com.lms.lms.model.Announcement.TargetAudience.STUDENTS));
        return "student/announcements";
    }
}
