package com.lms.lms.repository;

import com.lms.lms.model.AssignmentSubmission;
import com.lms.lms.model.Assignment;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.Comparator;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment(Assignment assignment);

    List<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, User student);

    default Optional<AssignmentSubmission> findLatestByAssignmentAndStudent(Assignment assignment, User student) {
        return findByAssignmentAndStudent(assignment, student).stream()
                .max(Comparator.comparing(AssignmentSubmission::getSubmittedAt));
    }

    List<AssignmentSubmission> findByStudent(User student);

    long countByAssignmentAndGraded(Assignment assignment, boolean graded);

    boolean existsByAssignmentAndStudent(Assignment assignment, User student);
}
