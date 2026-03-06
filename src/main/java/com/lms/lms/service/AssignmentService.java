package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    public List<Assignment> findByCourse(Course course) {
        return assignmentRepository.findByCourseOrderByDueDateDesc(course);
    }

    public List<Assignment> findByCourses(List<Course> courses) {
        return assignmentRepository.findByCourseIn(courses);
    }

    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Transactional
    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void delete(Long id) {
        assignmentRepository.deleteById(id);
    }

    // Submissions
    public List<AssignmentSubmission> getSubmissions(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    public Optional<AssignmentSubmission> getSubmission(Assignment assignment, User student) {
        return submissionRepository.findByAssignmentAndStudent(assignment, student);
    }

    public List<AssignmentSubmission> getStudentSubmissions(User student) {
        return submissionRepository.findByStudent(student);
    }

    public boolean hasSubmitted(Assignment assignment, User student) {
        return submissionRepository.existsByAssignmentAndStudent(assignment, student);
    }

    @Transactional
    public AssignmentSubmission submitAssignment(AssignmentSubmission submission) {
        return submissionRepository.save(submission);
    }

    @Transactional
    public AssignmentSubmission gradeSubmission(Long submissionId, int marks, String feedback) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setMarks(marks);
        submission.setFeedback(feedback);
        submission.setGraded(true);
        submission.setGradedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }
}
