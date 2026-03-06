package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final UserService userService;
    private final CourseService courseService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final AssignmentService assignmentService;
    private final ExamService examService;
    private final AttendanceService attendanceService;
    private final StudentGradeService studentGradeService;
    private final FileStorageService fileStorageService;
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
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courses.size());
        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        return "teacher/dashboard";
    }

    // === Course Management ===
    @GetMapping("/courses")
    public String courses(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("courses", courseService.findByTeacher(teacher));
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        return "teacher/courses";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("gradeId") Long gradeId,
            Authentication auth, RedirectAttributes ra) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = Course.builder()
                .title(title).description(description)
                .subject(subjectService.findById(subjectId).orElseThrow())
                .grade(gradeService.findById(gradeId).orElseThrow())
                .teacher(teacher).active(true)
                .build();
        courseService.save(course);
        ra.addFlashAttribute("success", "Course created!");
        return "redirect:/teacher/courses";
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable("id") Long id, Model model) {
        Course course = courseService.findById(id).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("materials", courseService.getMaterials(course));
        model.addAttribute("assignments", assignmentService.findByCourse(course));
        model.addAttribute("exams", examService.findByCourse(course));
        model.addAttribute("students", userService.findStudentsByGrade(course.getGrade()));
        return "teacher/course-detail";
    }

    // === Materials ===
    @PostMapping("/courses/{id}/material")
    public String uploadMaterial(@PathVariable("id") Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String desc,
            @RequestParam("file") MultipartFile file,
            Authentication auth, RedirectAttributes ra) {
        try {
            Course course = courseService.findById(courseId).orElseThrow();
            User teacher = userService.findByUsername(auth.getName()).orElseThrow();
            String filePath = fileStorageService.store(file, "materials");

            CourseMaterial material = CourseMaterial.builder()
                    .title(title).description(desc).course(course).uploadedBy(teacher)
                    .fileName(file.getOriginalFilename()).filePath(filePath)
                    .fileType(file.getContentType()).fileSize(file.getSize())
                    .build();
            courseService.saveMaterial(material);
            ra.addFlashAttribute("success", "Material uploaded!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/teacher/courses/" + courseId;
    }

    // === Assignments ===
    @GetMapping("/assignments/new/{courseId}")
    public String newAssignment(@PathVariable("courseId") Long courseId, Model model) {
        model.addAttribute("course", courseService.findById(courseId).orElseThrow());
        return "teacher/assignment-form";
    }

    @PostMapping("/assignments/save")
    public String saveAssignment(@RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("dueDate") LocalDate dueDate,
            @RequestParam(value = "maxMarks", defaultValue = "100") int maxMarks,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes ra) {
        try {
            Course course = courseService.findById(courseId).orElseThrow();
            Assignment assignment = Assignment.builder()
                    .title(title).description(description).dueDate(dueDate)
                    .maxMarks(maxMarks).course(course)
                    .build();
            if (file != null && !file.isEmpty()) {
                String path = fileStorageService.store(file, "assignments");
                assignment.setAttachmentPath(path);
                assignment.setAttachmentName(file.getOriginalFilename());
            }
            assignmentService.save(assignment);
            ra.addFlashAttribute("success", "Assignment created!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/teacher/courses/" + courseId;
    }

    @GetMapping("/assignments/{id}/submissions")
    public String viewSubmissions(@PathVariable("id") Long id, Model model) {
        Assignment assignment = assignmentService.findById(id).orElseThrow();
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", assignmentService.getSubmissions(assignment));
        return "teacher/submissions";
    }

    @PostMapping("/submissions/{id}/grade")
    public String gradeSubmission(@PathVariable("id") Long id,
            @RequestParam("marks") int marks,
            @RequestParam(value = "feedback", required = false) String feedback,
            RedirectAttributes ra) {
        AssignmentSubmission sub = assignmentService.gradeSubmission(id, marks, feedback);
        ra.addFlashAttribute("success", "Submission graded!");
        return "redirect:/teacher/assignments/" + sub.getAssignment().getId() + "/submissions";
    }

    // === Exams / Quizzes ===
    @GetMapping("/exams/new/{courseId}")
    public String newExam(@PathVariable("courseId") Long courseId, Model model) {
        model.addAttribute("course", courseService.findById(courseId).orElseThrow());
        return "teacher/exam-form";
    }

    @PostMapping("/exams/save")
    public String saveExam(@RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("type") Exam.ExamType type,
            @RequestParam(value = "examDate", required = false) LocalDate examDate,
            @RequestParam(value = "durationMinutes", defaultValue = "30") int duration,
            @RequestParam(value = "totalMarks", defaultValue = "100") int totalMarks,
            RedirectAttributes ra) {
        Course course = courseService.findById(courseId).orElseThrow();
        Exam exam = Exam.builder()
                .title(title).description(description).type(type)
                .examDate(examDate).durationMinutes(duration).totalMarks(totalMarks)
                .course(course).published(false)
                .build();
        examService.save(exam);
        ra.addFlashAttribute("success", "Exam created! Now add questions.");
        return "redirect:/teacher/exams/" + exam.getId() + "/questions";
    }

    @GetMapping("/exams/{id}/questions")
    public String examQuestions(@PathVariable("id") Long id, Model model) {
        Exam exam = examService.findById(id).orElseThrow();
        model.addAttribute("exam", exam);
        model.addAttribute("questions", examService.getQuestions(exam));
        return "teacher/exam-questions";
    }

    @PostMapping("/exams/{id}/questions/add")
    public String addQuestion(@PathVariable("id") Long examId,
            @RequestParam("questionText") String questionText,
            @RequestParam("optionA") String a,
            @RequestParam("optionB") String b,
            @RequestParam("optionC") String c,
            @RequestParam("optionD") String d,
            @RequestParam("correctAnswer") String correctAnswer,
            @RequestParam(value = "marks", defaultValue = "1") int marks,
            RedirectAttributes ra) {
        Exam exam = examService.findById(examId).orElseThrow();
        int order = examService.getQuestions(exam).size() + 1;
        ExamQuestion q = ExamQuestion.builder()
                .exam(exam).questionText(questionText)
                .optionA(a).optionB(b).optionC(c).optionD(d)
                .correctAnswer(correctAnswer).marks(marks).questionOrder(order)
                .build();
        examService.saveQuestion(q);
        ra.addFlashAttribute("success", "Question added!");
        return "redirect:/teacher/exams/" + examId + "/questions";
    }

    @GetMapping("/exams/{id}/publish")
    public String publishExam(@PathVariable("id") Long id, RedirectAttributes ra) {
        Exam exam = examService.findById(id).orElseThrow();
        exam.setPublished(true);
        examService.save(exam);
        ra.addFlashAttribute("success", "Exam published!");
        return "redirect:/teacher/courses/" + exam.getCourse().getId();
    }

    @GetMapping("/exams/{id}/results")
    public String examResults(@PathVariable("id") Long id, Model model) {
        Exam exam = examService.findById(id).orElseThrow();
        model.addAttribute("exam", exam);
        model.addAttribute("attempts", examService.getAttempts(exam));
        return "teacher/exam-results";
    }

    // === Attendance ===
    @GetMapping("/attendance/{courseId}")
    public String attendancePage(@PathVariable("courseId") Long courseId, Model model) {
        Course course = courseService.findById(courseId).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("students", userService.findStudentsByGrade(course.getGrade()));
        model.addAttribute("today", LocalDate.now());
        List<Attendance> todayAttendance = attendanceService.findByCourseAndDate(course, LocalDate.now());
        model.addAttribute("todayAttendance", todayAttendance);
        return "teacher/attendance";
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(@RequestParam("courseId") Long courseId,
            @RequestParam("date") LocalDate date,
            @RequestParam Map<String, String> params,
            RedirectAttributes ra) {
        Course course = courseService.findById(courseId).orElseThrow();
        List<Attendance> records = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("status_")) {
                Long studentId = Long.parseLong(entry.getKey().replace("status_", ""));
                User student = userService.findById(studentId).orElse(null);
                if (student != null) {
                    Attendance att = Attendance.builder()
                            .student(student).course(course).date(date)
                            .status(Attendance.AttendanceStatus.valueOf(entry.getValue()))
                            .build();
                    records.add(att);
                }
            }
        }
        attendanceService.saveAll(records);
        ra.addFlashAttribute("success", "Attendance saved!");
        return "redirect:/teacher/attendance/" + courseId;
    }

    // === Students ===
    @GetMapping("/students")
    public String myStudents(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        Map<Grade, List<User>> studentsByGrade = new LinkedHashMap<>();
        for (Course c : courses) {
            List<User> students = userService.findStudentsByGrade(c.getGrade());
            if (!students.isEmpty()) {
                studentsByGrade.put(c.getGrade(), students);
            }
        }
        model.addAttribute("studentsByGrade", studentsByGrade);
        return "teacher/my-students";
    }

    // === Grading ===
    @GetMapping("/grading/{courseId}")
    public String gradingPage(@PathVariable("courseId") Long courseId, Model model) {
        Course course = courseService.findById(courseId).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("students", userService.findStudentsByGrade(course.getGrade()));
        model.addAttribute("grades", studentGradeService.findByCourse(course));
        return "teacher/grading";
    }

    @PostMapping("/grading/save")
    public String saveGrading(@RequestParam("courseId") Long courseId,
            @RequestParam("studentId") Long studentId,
            @RequestParam("examName") String examName,
            @RequestParam("marks") double marks,
            @RequestParam("maxMarks") double maxMarks,
            @RequestParam(value = "term", defaultValue = "Term 1") String term,
            @RequestParam(value = "remarks", required = false) String remarks,
            Authentication auth, RedirectAttributes ra) {
        Course course = courseService.findById(courseId).orElseThrow();
        User student = userService.findById(studentId).orElseThrow();
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();

        StudentGrade grade = StudentGrade.builder()
                .student(student).subject(course.getSubject()).course(course)
                .examName(examName).marks(marks).maxMarks(maxMarks)
                .term(term).remarks(remarks).recordedBy(teacher)
                .build();
        studentGradeService.save(grade);
        ra.addFlashAttribute("success", "Grade recorded!");
        return "redirect:/teacher/grading/" + courseId;
    }
}
