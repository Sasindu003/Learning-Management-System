package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.dto.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final TimetableService timetableService;
    private final ActivityLogService activityLogService;
    private final AcademicTermService termService;


    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courses.size());
        
        // Pinned courses (limit 4)
        List<Course> pinned = new ArrayList<>(teacher.getPinnedCourses());
        if (pinned.size() > 4) {
            pinned = pinned.subList(0, 4);
        }
        model.addAttribute("pinnedCourses", pinned);

        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        
        // Activity Feed (limit 15)
        model.addAttribute("recentActivities", activityLogService.getRecentActivitiesForCourses(courses, 15));
        
        return "teacher/dashboard";
    }

    // === Course Management ===
    @GetMapping("/courses")
    public String courses(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("courses", courseService.findByTeacher(teacher));
        model.addAttribute("grades", teacher.getGrades());
        model.addAttribute("subjects", teacher.getSubjects());
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
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "externalUrl", required = false) String externalUrl,
            Authentication auth, RedirectAttributes ra) {
        try {
            Course course = courseService.findById(courseId).orElseThrow();
            User teacher = userService.findByUsername(auth.getName()).orElseThrow();

            CourseMaterial material = CourseMaterial.builder()
                    .title(title).description(desc).course(course).uploadedBy(teacher)
                    .build();

            if (file != null && !file.isEmpty()) {
                String filePath = fileStorageService.store(file, "materials");
                material.setFileName(file.getOriginalFilename());
                material.setFilePath(filePath);
                material.setFileType(file.getContentType());
                material.setFileSize(file.getSize());
            } else if (externalUrl != null && !externalUrl.isEmpty()) {
                material.setExternalUrl(externalUrl);
            } else {
                throw new RuntimeException("Either file or external URL is required");
            }

            courseService.saveMaterial(material);
            ra.addFlashAttribute("success", "Material added!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed: " + e.getMessage());
        }
        return "redirect:/teacher/courses/" + courseId;
    }

    @GetMapping("/materials/edit/{id}")
    public String editMaterial(@PathVariable("id") Long id, Model model) {
        CourseMaterial material = courseService.findMaterialById(id).orElseThrow();
        model.addAttribute("material", material);
        return "teacher/material-form";
    }

    @PostMapping("/materials/update")
    public String updateMaterial(@RequestParam("id") Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String desc,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "externalUrl", required = false) String externalUrl,
            RedirectAttributes ra) {
        try {
            CourseMaterial material = courseService.findMaterialById(id).orElseThrow();
            material.setTitle(title);
            material.setDescription(desc);

            if (file != null && !file.isEmpty()) {
                String filePath = fileStorageService.store(file, "materials");
                material.setFileName(file.getOriginalFilename());
                material.setFilePath(filePath);
                material.setFileType(file.getContentType());
                material.setFileSize(file.getSize());
                material.setExternalUrl(null); // Clear URL if file uploaded
            } else if (externalUrl != null && !externalUrl.isEmpty()) {
                material.setExternalUrl(externalUrl);
                material.setFilePath(null); // Clear file if URL provided
                material.setFileName(null);
            }

            courseService.saveMaterial(material);
            ra.addFlashAttribute("success", "Material updated!");
            return "redirect:/teacher/courses/" + material.getCourse().getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Update failed: " + e.getMessage());
            return "redirect:/teacher/materials/edit/" + id;
        }
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
            @RequestParam("dueDate") LocalDateTime dueDate,
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

    @GetMapping("/assignments/edit/{id}")
    public String editAssignment(@PathVariable("id") Long id, Model model) {
        Assignment assignment = assignmentService.findById(id).orElseThrow();
        model.addAttribute("assignment", assignment);
        model.addAttribute("course", assignment.getCourse());
        return "teacher/assignment-form";
    }

    @PostMapping("/assignments/update")
    public String updateAssignment(@RequestParam("id") Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("dueDate") LocalDateTime dueDate,
            @RequestParam(value = "maxMarks", defaultValue = "100") int maxMarks,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes ra) {
        try {
            Assignment assignment = assignmentService.findById(id).orElseThrow();
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setDueDate(dueDate);
            assignment.setMaxMarks(maxMarks);

            if (file != null && !file.isEmpty()) {
                String path = fileStorageService.store(file, "assignments");
                assignment.setAttachmentPath(path);
                assignment.setAttachmentName(file.getOriginalFilename());
            }

            assignmentService.save(assignment);
            ra.addFlashAttribute("success", "Assignment updated!");
            return "redirect:/teacher/courses/" + assignment.getCourse().getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Update failed: " + e.getMessage());
            return "redirect:/teacher/assignments/edit/" + id;
        }
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
            @RequestParam(value = "examDate", required = false) LocalDateTime examDate,
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

    @GetMapping("/exams/questions/edit/{id}")
    public String editQuestion(@PathVariable("id") Long id, Model model) {
        ExamQuestion question = examService.findQuestionById(id).orElseThrow();
        model.addAttribute("question", question);
        model.addAttribute("exam", question.getExam());
        return "teacher/exam-question-edit";
    }

    @PostMapping("/exams/questions/update")
    public String updateQuestion(@RequestParam("id") Long id,
            @RequestParam("questionText") String questionText,
            @RequestParam("optionA") String a,
            @RequestParam("optionB") String b,
            @RequestParam("optionC") String c,
            @RequestParam("optionD") String d,
            @RequestParam("correctAnswer") String correctAnswer,
            @RequestParam(value = "marks", defaultValue = "1") int marks,
            RedirectAttributes ra) {
        ExamQuestion q = examService.findQuestionById(id).orElseThrow();
        q.setQuestionText(questionText);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrectAnswer(correctAnswer);
        q.setMarks(marks);
        examService.saveQuestion(q);
        ra.addFlashAttribute("success", "Question updated!");
        return "redirect:/teacher/exams/" + q.getExam().getId() + "/questions";
    }

    @GetMapping("/exams/questions/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            ExamQuestion q = examService.findQuestionById(id).orElseThrow();
            Long examId = q.getExam().getId();
            examService.deleteQuestion(id);
            ra.addFlashAttribute("success", "Question deleted!");
            return "redirect:/teacher/exams/" + examId + "/questions";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Cannot delete question. It may have existing answers from students.");
            return "redirect:/teacher/dashboard"; // Fallback, though ideally we'd have the examId
        }
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
        Map<Long, StudentAssignmentProgress> progressStats = new HashMap<>();

        for (Course c : courses) {
            List<User> students = userService.findStudentsByGrade(c.getGrade());
            if (!students.isEmpty()) {
                studentsByGrade.put(c.getGrade(), students);

                List<Assignment> teacherAssignments = assignmentService.findByCourse(c);
                for (User s : students) {
                    // Safety check: ensure student's grade matches course grade ID
                    if (s.getGrade() == null || c.getGrade() == null || 
                        !s.getGrade().getId().equals(c.getGrade().getId())) continue;

                    StudentAssignmentProgress stats = progressStats.getOrDefault(s.getId(),
                            StudentAssignmentProgress.builder()
                                    .studentId(s.getId()).totalCount(0).completedCount(0).build());

                    stats.setTotalCount(stats.getTotalCount() + teacherAssignments.size());
                    int completed = 0;
                    for (Assignment a : teacherAssignments) {
                        if (assignmentService.hasSubmitted(a, s)) {
                            completed++;
                        }
                    }
                    stats.setCompletedCount(stats.getCompletedCount() + completed);
                    progressStats.put(s.getId(), stats);
                }
            }
        }
        model.addAttribute("studentsByGrade", studentsByGrade);
        model.addAttribute("progressStats", progressStats);
        return "teacher/my-students";
    }

    @GetMapping("/api/students/{id}/progress")
    @ResponseBody
    public List<AssignmentProgressDetail> getStudentProgress(@PathVariable("id") Long studentId, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        User student = userService.findById(studentId).orElseThrow();
        List<Course> teacherCourses = courseService.findByTeacher(teacher);

        List<AssignmentProgressDetail> details = new ArrayList<>();
        for (Course c : teacherCourses) {
            // Compare Grade IDs instead of objects to avoid JPA proxy comparison issues
            if (c.getGrade() != null && student.getGrade() != null &&
                c.getGrade().getId().equals(student.getGrade().getId())) {
                List<Assignment> assignments = assignmentService.findByCourse(c);
                for (Assignment a : assignments) {
                    Optional<AssignmentSubmission> sub = assignmentService.getSubmission(a, student);
                    details.add(AssignmentProgressDetail.builder()
                            .title(a.getTitle() + " (" + c.getSubject().getName() + ")")
                            .dueDate(a.getDueDate())
                            .submitted(sub.isPresent())
                            .submittedAt(sub.map(AssignmentSubmission::getSubmittedAt).orElse(null))
                            .marks(sub.map(AssignmentSubmission::getMarks).orElse(null))
                            .graded(sub.map(AssignmentSubmission::isGraded).orElse(false))
                            .build());
                }
            }
        }
        // Sort by due date (newest first)
        details.sort((a, b) -> b.getDueDate().compareTo(a.getDueDate()));
        return details;
    }

    // === Grading ===
    @GetMapping("/grading/{courseId}")
    public String gradingPage(@PathVariable("courseId") Long courseId, Model model) {
        Course course = courseService.findById(courseId).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("students", userService.findStudentsByGrade(course.getGrade()));
        model.addAttribute("grades", studentGradeService.findByCourse(course));
        model.addAttribute("allTerms", termService.findAll());
        
        String defaultTermName = termService.getTermNameForDate(java.time.LocalDate.now());
        if (defaultTermName.startsWith("Other") && !termService.findActive().isEmpty()) {
            defaultTermName = termService.findActive().get(0).getName();
        }
        model.addAttribute("defaultTermName", defaultTermName);
        
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

    // === Timetable ===
    @GetMapping("/timetable")
    public String timetable(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("timetable", timetableService.findByTeacher(teacher));
        return "teacher/timetable";
    }

    // === Announcements View ===
    @GetMapping("/announcements")
    public String announcements(Model model) {
        model.addAttribute("announcements", announcementService.findByTarget(com.lms.lms.model.Announcement.TargetAudience.TEACHERS));
        return "teacher/announcements";
    }

    // === Delete Assignment ===
    @GetMapping("/assignments/delete/{id}")
    public String deleteAssignment(@PathVariable("id") Long id, RedirectAttributes ra) {
        Assignment assignment = assignmentService.findById(id).orElseThrow();
        Long courseId = assignment.getCourse().getId();
        assignmentService.delete(id);
        ra.addFlashAttribute("success", "Assignment deleted!");
        return "redirect:/teacher/courses/" + courseId;
    }

    // === Delete Exam ===
    @GetMapping("/exams/delete/{id}")
    public String deleteExam(@PathVariable("id") Long id, RedirectAttributes ra) {
        Exam exam = examService.findById(id).orElseThrow();
        Long courseId = exam.getCourse().getId();
        examService.delete(id);
        ra.addFlashAttribute("success", "Exam deleted!");
        return "redirect:/teacher/courses/" + courseId;
    }

    // === Delete Material ===
    @GetMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable("id") Long id, RedirectAttributes ra) {
        CourseMaterial material = courseService.findMaterialById(id).orElseThrow();
        Long courseId = material.getCourse().getId();
        courseService.deleteMaterial(id);
        ra.addFlashAttribute("success", "Material deleted!");
        return "redirect:/teacher/courses/" + courseId;
    }

    // === Attendance History ===
    @GetMapping("/attendance/{courseId}/history")
    public String attendanceHistory(@PathVariable("courseId") Long courseId,
            @RequestParam(value = "date", required = false) LocalDate date,
            Model model) {
        Course course = courseService.findById(courseId).orElseThrow();
        LocalDate viewDate = date != null ? date : LocalDate.now();
        model.addAttribute("course", course);
        model.addAttribute("students", userService.findStudentsByGrade(course.getGrade()));
        model.addAttribute("viewDate", viewDate);
        model.addAttribute("attendanceRecords", attendanceService.findByCourseAndDate(course, viewDate));
        return "teacher/attendance-history";
    }
}
