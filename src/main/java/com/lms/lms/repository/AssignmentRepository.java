package com.lms.lms.repository;

import com.lms.lms.entity.Assignment;
import com.lms.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AssignmentRepository — handles database operations for assignments.
 *
 * Custom query: findBySubject()
 * - Students can filter assignments by subject.
 * - Teachers can see their assignments for a specific subject.
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * Find all assignments for a specific subject.
     * Spring Data JPA auto-generates: SELECT * FROM assignments WHERE subject_id =
     * ?
     */
    List<Assignment> findBySubject(Subject subject);
}
