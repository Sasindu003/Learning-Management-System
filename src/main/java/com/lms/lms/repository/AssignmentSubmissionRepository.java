package com.lms.lms.repository;

import com.lms.lms.model.AssignmentSubmission;
import com.lms.lms.model.Assignment;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment(Assignment assignment);

    Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, User student);

    List<AssignmentSubmission> findByStudent(User student);

    long countByAssignmentAndGraded(Assignment assignment, boolean graded);

    boolean existsByAssignmentAndStudent(Assignment assignment, User student);
}
