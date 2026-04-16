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

    @org.springframework.data.jpa.repository.Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.course.teacher = :teacher AND s.student.grade IN :grades ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findRecentByTeacherAndGrades(
        @org.springframework.data.repository.query.Param("teacher") com.lms.lms.model.User teacher, 
        @org.springframework.data.repository.query.Param("grades") Set<com.lms.lms.model.Grade> grades, 
        org.springframework.data.domain.Pageable pageable
    );
}
