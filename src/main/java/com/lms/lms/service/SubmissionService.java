package com.lms.lms.service;

import com.lms.lms.entity.Assignment;
import com.lms.lms.entity.Submission;
import com.lms.lms.entity.User;
import com.lms.lms.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SubmissionService — business logic for assignment submissions.
 *
 * Key feature: checking if a student already submitted, to prevent duplicates.
 */
@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    /** Get all submissions for a specific assignment — teacher reviews these */
    public List<Submission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    /** Get all submissions by a specific student */
    public List<Submission> getSubmissionsByStudent(User student) {
        return submissionRepository.findByStudent(student);
    }

    /**
     * Check if a student already submitted for an assignment.
     * Returns the existing submission if found.
     */
    public Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student) {
        return submissionRepository.findByAssignmentAndStudent(assignment, student);
    }

    /** Save a new submission */
    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }
}
