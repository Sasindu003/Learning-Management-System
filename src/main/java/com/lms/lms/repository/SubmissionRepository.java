package com.lms.lms.repository;

import com.lms.lms.entity.Assignment;
import com.lms.lms.entity.Submission;
import com.lms.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SubmissionRepository — handles database operations for assignment
 * submissions.
 *
 * Custom queries:
 * - findByAssignment: Get all submissions for a specific assignment (teacher
 * view).
 * - findByStudent: Get all submissions by a specific student.
 * - findByAssignmentAndStudent: Check if a student already submitted (prevent
 * duplicates).
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /** Get all submissions for an assignment — used by teachers to review work */
    List<Submission> findByAssignment(Assignment assignment);

    /** Get all submissions by a student — used for student history */
    List<Submission> findByStudent(User student);

    /** Check if a student already submitted for a specific assignment */
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student);
}
