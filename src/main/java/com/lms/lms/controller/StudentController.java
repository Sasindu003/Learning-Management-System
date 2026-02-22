package com.lms.lms.controller;

import com.lms.lms.entity.*;
import com.lms.lms.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * StudentController — handles student-only pages.
 *
 * Sprint 0: View lessons and assignments (read-only).
 * Sprint 1 additions:
 * - Submit answers to assignments (text or file upload)
 * - Take online quizzes and see results
 * - View class announcements from teachers
 *
 * All URLs start with /student/ — only STUDENT role can access.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    private final LessonNoteService lessonNoteService;
    private final AssignmentService assignmentService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final SubmissionService submissionService;
    private final QuizService quizService;
    private final ClassAnnouncementService classAnnouncementService;

    private static final String UPLOAD_DIR = "uploads/submissions/";

    public StudentController(LessonNoteService lessonNoteService,
            AssignmentService assignmentService,
            SubjectService subjectService,
            UserService userService,
            SubmissionService submissionService,
            QuizService quizService,
            ClassAnnouncementService classAnnouncementService) {
        this.lessonNoteService = lessonNoteService;
        this.assignmentService = assignmentService;
        this.subjectService = subjectService;
        this.userService = userService;
        this.submissionService = submissionService;
        this.quizService = quizService;
        this.classAnnouncementService = classAnnouncementService;
    }

    // ==================== LESSONS ====================

    /** View all lesson notes */
    @GetMapping("/lessons")
    public String viewLessons(Model model) {
        model.addAttribute("lessons", lessonNoteService.getAllLessonNotes());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "student/lessons";
    }

    // ==================== ASSIGNMENTS & SUBMISSIONS ====================

    /** View all assignments */
    @GetMapping("/assignments")
    public String viewAssignments(Model model, Authentication authentication) {
        User student = userService.findByUsername(authentication.getName()).orElse(null);
        List<Assignment> assignments = assignmentService.getAllAssignments();

        // Build a map of assignmentId → whether this student already submitted
        // This lets the template show "Submitted ✅" or "Submit" buttons
        Map<Long, Boolean> submittedMap = new HashMap<>();
        for (Assignment a : assignments) {
            boolean submitted = submissionService.findByAssignmentAndStudent(a, student).isPresent();
            submittedMap.put(a.getId(), submitted);
        }

        model.addAttribute("assignments", assignments);
        model.addAttribute("submittedMap", submittedMap);
        return "student/assignments";
    }

    /**
     * View a specific assignment + submission form.
     * If the student already submitted, show their submission instead.
     */
    @GetMapping("/assignments/{id}")
    public String assignmentDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        if (assignment == null)
            return "redirect:/student/assignments";

        User student = userService.findByUsername(authentication.getName()).orElse(null);
        Optional<Submission> existing = submissionService.findByAssignmentAndStudent(assignment, student);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", existing.orElse(null));
        model.addAttribute("alreadySubmitted", existing.isPresent());
        return "student/assignment-detail";
    }

    /**
     * Submit an answer to an assignment.
     * Students can submit text, a file, or both.
     * Only one submission per student per assignment is allowed.
     */
    @PostMapping("/assignments/{id}/submit")
    public String submitAssignment(@PathVariable Long id,
            @RequestParam(required = false) String answerText,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        User student = userService.findByUsername(authentication.getName()).orElse(null);

        if (assignment == null || student == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid assignment.");
            return "redirect:/student/assignments";
        }

        // Prevent duplicate submissions
        if (submissionService.findByAssignmentAndStudent(assignment, student).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "You have already submitted this assignment.");
            return "redirect:/student/assignments/" + id;
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setAnswerText(answerText);

        // Handle file upload if provided
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);

                String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                submission.setFilePath(filePath.toString());
                submission.setFileName(file.getOriginalFilename());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
                return "redirect:/student/assignments/" + id;
            }
        }

        submissionService.saveSubmission(submission);
        redirectAttributes.addFlashAttribute("success", "Assignment submitted successfully!");
        return "redirect:/student/assignments/" + id;
    }

    // ==================== QUIZZES ====================

    /** View all available quizzes */
    @GetMapping("/quizzes")
    public String viewQuizzes(Model model, Authentication authentication) {
        User student = userService.findByUsername(authentication.getName()).orElse(null);
        List<Quiz> quizzes = quizService.getAllQuizzes();

        // Check which quizzes this student has already attempted
        Map<Long, QuizAttempt> attemptMap = new HashMap<>();
        for (Quiz quiz : quizzes) {
            quizService.findAttempt(quiz, student).ifPresent(attempt -> attemptMap.put(quiz.getId(), attempt));
        }

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("attemptMap", attemptMap);
        return "student/quizzes";
    }

    /**
     * Show the quiz-taking page.
     * Displays all questions with radio buttons for A/B/C/D.
     * If the student already took this quiz, redirect to results.
     */
    @GetMapping("/quizzes/{id}")
    public String takeQuiz(@PathVariable Long id, Model model, Authentication authentication) {
        Quiz quiz = quizService.findQuizById(id).orElse(null);
        if (quiz == null)
            return "redirect:/student/quizzes";

        User student = userService.findByUsername(authentication.getName()).orElse(null);

        // If already attempted, show the result page instead
        Optional<QuizAttempt> existing = quizService.findAttempt(quiz, student);
        if (existing.isPresent()) {
            model.addAttribute("quiz", quiz);
            model.addAttribute("attempt", existing.get());
            return "student/quiz-result";
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quizService.getQuestionsByQuiz(quiz));
        return "student/take-quiz";
    }

    /**
     * Submit quiz answers and calculate score.
     * Answers come as request parameters named "answer_{questionId}" with values
     * A/B/C/D.
     */
    @PostMapping("/quizzes/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
            @RequestParam Map<String, String> allParams,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        Quiz quiz = quizService.findQuizById(id).orElse(null);
        User student = userService.findByUsername(authentication.getName()).orElse(null);

        if (quiz == null || student == null) {
            return "redirect:/student/quizzes";
        }

        // Prevent retakes
        if (quizService.findAttempt(quiz, student).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "You have already taken this quiz.");
            return "redirect:/student/quizzes";
        }

        // Extract answers from the form: "answer_1" → "A", "answer_2" → "C", etc.
        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("answer_")) {
                Long questionId = Long.parseLong(entry.getKey().replace("answer_", ""));
                answers.put(questionId, entry.getValue());
            }
        }

        // Grade the quiz
        List<QuizQuestion> questions = quizService.getQuestionsByQuiz(quiz);
        int score = quizService.gradeQuiz(questions, answers);

        // Save the attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setScore(score);
        attempt.setTotalQuestions(questions.size());
        quizService.saveAttempt(attempt);

        // Show the result page
        model.addAttribute("quiz", quiz);
        model.addAttribute("attempt", attempt);
        return "student/quiz-result";
    }
}
