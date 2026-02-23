package com.lms.lms.controller;

import com.lms.lms.entity.Assignment;
import com.lms.lms.entity.ClassAnnouncement;
import com.lms.lms.entity.LessonNote;
import com.lms.lms.entity.Quiz;
import com.lms.lms.entity.QuizQuestion;
import com.lms.lms.entity.Subject;
import com.lms.lms.entity.User;
import com.lms.lms.service.AssignmentService;
import com.lms.lms.service.ClassAnnouncementService;
import com.lms.lms.service.LessonNoteService;
import com.lms.lms.service.QuizService;
import com.lms.lms.service.SubjectService;
import com.lms.lms.service.SubmissionService;
import com.lms.lms.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * TeacherController — handles all teacher-only pages.
 *
 * Sprint 0: Upload lesson notes, create assignments (with delete).
 * Sprint 1 additions:
 * - EDIT lesson notes and assignments
 * - Create quizzes with multiple-choice questions
 * - Post class-specific announcements
 * - View student submissions for assignments
 *
 * All URLs start with /teacher/ — only TEACHER role can access.
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final LessonNoteService lessonNoteService;
    private final AssignmentService assignmentService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final QuizService quizService;
    private final SubmissionService submissionService;
    private final ClassAnnouncementService classAnnouncementService;

    /** Where uploaded files are stored on the server */
    private static final String UPLOAD_DIR = "uploads/";

    public TeacherController(LessonNoteService lessonNoteService,
            AssignmentService assignmentService,
            SubjectService subjectService,
            UserService userService,
            QuizService quizService,
            SubmissionService submissionService,
            ClassAnnouncementService classAnnouncementService) {
        this.lessonNoteService = lessonNoteService;
        this.assignmentService = assignmentService;
        this.subjectService = subjectService;
        this.userService = userService;
        this.quizService = quizService;
        this.submissionService = submissionService;
        this.classAnnouncementService = classAnnouncementService;
    }

    // ==================== LESSON NOTES ====================

    /** Show the lesson notes page — upload form + list of uploaded notes */
    @GetMapping("/lessons")
    public String lessonsPage(Model model) {
        model.addAttribute("lessons", lessonNoteService.getAllLessonNotes());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher/lesson-notes";
    }

    /** Handle lesson note file upload */
    @PostMapping("/lessons")
    public String uploadLesson(@RequestParam String title,
            @RequestParam Long subjectId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
                return "redirect:/teacher/lessons";
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Subject subject = subjectService.findById(subjectId).orElse(null);
            User teacher = userService.findByUsername(authentication.getName()).orElse(null);

            LessonNote lessonNote = new LessonNote();
            lessonNote.setTitle(title);
            lessonNote.setFilePath(filePath.toString());
            lessonNote.setFileName(originalFilename);
            lessonNote.setSubject(subject);
            lessonNote.setUploadedBy(teacher);
            lessonNoteService.saveLessonNote(lessonNote);

            redirectAttributes.addFlashAttribute("success", "Lesson note uploaded successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        return "redirect:/teacher/lessons";
    }

    /**
     * Show the edit form for a lesson note.
     * Pre-fills the form with the existing lesson data.
     */
    @GetMapping("/lessons/edit/{id}")
    public String editLessonPage(@PathVariable Long id, Model model) {
        LessonNote lesson = lessonNoteService.findById(id).orElse(null);
        if (lesson == null)
            return "redirect:/teacher/lessons";

        model.addAttribute("lesson", lesson);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher/edit-lesson";
    }

    /**
     * Handle the lesson note edit form submission.
     * Updates title and subject. Optionally replaces the file.
     */
    @PostMapping("/lessons/edit/{id}")
    public String editLesson(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam Long subjectId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        LessonNote lesson = lessonNoteService.findById(id).orElse(null);
        if (lesson == null) {
            redirectAttributes.addFlashAttribute("error", "Lesson note not found.");
            return "redirect:/teacher/lessons";
        }

        lesson.setTitle(title);
        lesson.setSubject(subjectService.findById(subjectId).orElse(null));

        // Only replace the file if a new one was uploaded
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);

                String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                lesson.setFilePath(filePath.toString());
                lesson.setFileName(file.getOriginalFilename());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload new file: " + e.getMessage());
                return "redirect:/teacher/lessons";
            }
        }

        lessonNoteService.saveLessonNote(lesson);
        redirectAttributes.addFlashAttribute("success", "Lesson note updated successfully!");
        return "redirect:/teacher/lessons";
    }

    /** Delete a lesson note */
    @PostMapping("/lessons/delete/{id}")
    public String deleteLesson(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        lessonNoteService.deleteLessonNote(id);
        redirectAttributes.addFlashAttribute("success", "Lesson note deleted successfully!");
        return "redirect:/teacher/lessons";
    }

    // ==================== ASSIGNMENTS ====================

    /** Show the assignments page — create form + list of existing assignments */
    @GetMapping("/assignments")
    public String assignmentsPage(Model model) {
        model.addAttribute("assignments", assignmentService.getAllAssignments());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher/assignments";
    }

    /** Handle "Create Assignment" form submission */
    @PostMapping("/assignments")
    public String createAssignment(@RequestParam String title,
            @RequestParam String description,
            @RequestParam LocalDate dueDate,
            @RequestParam Long subjectId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Subject subject = subjectService.findById(subjectId).orElse(null);
        User teacher = userService.findByUsername(authentication.getName()).orElse(null);

        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setSubject(subject);
        assignment.setCreatedBy(teacher);
        assignmentService.saveAssignment(assignment);

        redirectAttributes.addFlashAttribute("success", "Assignment created successfully!");
        return "redirect:/teacher/assignments";
    }

    /** Show the edit form for an assignment */
    @GetMapping("/assignments/edit/{id}")
    public String editAssignmentPage(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        if (assignment == null)
            return "redirect:/teacher/assignments";

        model.addAttribute("assignment", assignment);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher/edit-assignment";
    }

    /** Handle the assignment edit form submission */
    @PostMapping("/assignments/edit/{id}")
    public String editAssignment(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam LocalDate dueDate,
            @RequestParam Long subjectId,
            RedirectAttributes redirectAttributes) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        if (assignment == null) {
            redirectAttributes.addFlashAttribute("error", "Assignment not found.");
            return "redirect:/teacher/assignments";
        }

        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setSubject(subjectService.findById(subjectId).orElse(null));
        assignmentService.saveAssignment(assignment);

        redirectAttributes.addFlashAttribute("success", "Assignment updated successfully!");
        return "redirect:/teacher/assignments";
    }

    /** Delete an assignment */
    @PostMapping("/assignments/delete/{id}")
    public String deleteAssignment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        assignmentService.deleteAssignment(id);
        redirectAttributes.addFlashAttribute("success", "Assignment deleted successfully!");
        return "redirect:/teacher/assignments";
    }

    /** View all student submissions for a specific assignment */
    @GetMapping("/assignments/{id}/submissions")
    public String viewSubmissions(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        if (assignment == null)
            return "redirect:/teacher/assignments";

        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", submissionService.getSubmissionsByAssignment(assignment));
        return "teacher/submissions";
    }

    // ==================== QUIZZES ====================

    /** Show the quizzes page — create form + list of existing quizzes */
    @GetMapping("/quizzes")
    public String quizzesPage(Model model) {
        model.addAttribute("quizzes", quizService.getAllQuizzes());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher/quizzes";
    }

    /** Handle "Create Quiz" form submission */
    @PostMapping("/quizzes")
    public String createQuiz(@RequestParam String title,
            @RequestParam Long subjectId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Subject subject = subjectService.findById(subjectId).orElse(null);
        User teacher = userService.findByUsername(authentication.getName()).orElse(null);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setSubject(subject);
        quiz.setCreatedBy(teacher);
        quizService.saveQuiz(quiz);

        // After creating a quiz, redirect to add questions
        redirectAttributes.addFlashAttribute("success", "Quiz created! Now add questions.");
        return "redirect:/teacher/quizzes/" + quiz.getId() + "/questions";
    }

    /** Show the page to add questions to a quiz */
    @GetMapping("/quizzes/{id}/questions")
    public String quizQuestionsPage(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findQuizById(id).orElse(null);
        if (quiz == null)
            return "redirect:/teacher/quizzes";

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quizService.getQuestionsByQuiz(quiz));
        return "teacher/quiz-questions";
    }

    /** Handle "Add Question" form submission */
    @PostMapping("/quizzes/{id}/questions")
    public String addQuestion(@PathVariable Long id,
            @RequestParam String questionText,
            @RequestParam String optionA,
            @RequestParam String optionB,
            @RequestParam String optionC,
            @RequestParam String optionD,
            @RequestParam String correctOption,
            RedirectAttributes redirectAttributes) {
        Quiz quiz = quizService.findQuizById(id).orElse(null);
        if (quiz == null)
            return "redirect:/teacher/quizzes";

        QuizQuestion question = new QuizQuestion();
        question.setQuestionText(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectOption(correctOption);
        question.setQuiz(quiz);
        quizService.saveQuestion(question);

        redirectAttributes.addFlashAttribute("success", "Question added!");
        return "redirect:/teacher/quizzes/" + id + "/questions";
    }

    /** Delete a question from a quiz */
    @PostMapping("/quizzes/{quizId}/questions/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long quizId,
            @PathVariable Long questionId,
            RedirectAttributes redirectAttributes) {
        quizService.deleteQuestion(questionId);
        redirectAttributes.addFlashAttribute("success", "Question deleted!");
        return "redirect:/teacher/quizzes/" + quizId + "/questions";
    }

    /** View student results for a quiz */
    @GetMapping("/quizzes/{id}/results")
    public String quizResults(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findQuizById(id).orElse(null);
        if (quiz == null)
            return "redirect:/teacher/quizzes";

        model.addAttribute("quiz", quiz);
        model.addAttribute("attempts", quizService.getAttemptsByQuiz(quiz));
        return "teacher/quiz-results";
    }

    /** Delete a quiz */
    @PostMapping("/quizzes/delete/{id}")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.deleteQuiz(id);
        redirectAttributes.addFlashAttribute("success", "Quiz deleted successfully!");
        return "redirect:/teacher/quizzes";
    }

    // ==================== CLASS ANNOUNCEMENTS ====================

    /** Show class announcements page */
    @GetMapping("/announcements")
    public String classAnnouncementsPage(Model model) {
        model.addAttribute("announcements", classAnnouncementService.getAllSorted());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "teacher/announcements";
    }

    /** Post a class-specific announcement */
    @PostMapping("/announcements")
    public String postClassAnnouncement(@RequestParam String title,
            @RequestParam String content,
            @RequestParam Long subjectId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Subject subject = subjectService.findById(subjectId).orElse(null);
        User teacher = userService.findByUsername(authentication.getName()).orElse(null);

        ClassAnnouncement announcement = new ClassAnnouncement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setSubject(subject);
        announcement.setTeacher(teacher);
        classAnnouncementService.save(announcement);

        redirectAttributes.addFlashAttribute("success", "Class announcement posted!");
        return "redirect:/teacher/announcements";
    }

    /** Delete a class announcement */
    @PostMapping("/announcements/delete/{id}")
    public String deleteClassAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        classAnnouncementService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Announcement deleted!");
        return "redirect:/teacher/announcements";
    }
}
