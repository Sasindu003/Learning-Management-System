package com.lms.lms.service;

import com.lms.lms.entity.Assignment;
import com.lms.lms.entity.Subject;
import com.lms.lms.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * AssignmentService — business logic for assignments.
 *
 * Provides simple CRUD + subject filtering.
 */
@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /** Get all assignments */
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    /** Get assignments filtered by subject */
    public List<Assignment> getAssignmentsBySubject(Subject subject) {
        return assignmentRepository.findBySubject(subject);
    }

    /** Find an assignment by ID */
    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    /** Save a new assignment or update an existing one */
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    /** Delete an assignment by ID */
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }
}
