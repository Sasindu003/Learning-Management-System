package com.lms.lms.repository;

import com.lms.lms.entity.Grade;
import com.lms.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SubjectRepository — handles database operations for subjects.
 *
 * Custom query: findByGrade()
 * - Lets us filter subjects by grade (e.g., "show all subjects in Grade 10").
 * - Used by teachers when choosing which subject to upload a lesson for.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Find all subjects that belong to a specific grade.
     * Spring Data JPA auto-generates: SELECT * FROM subjects WHERE grade_id = ?
     */
    List<Subject> findByGrade(Grade grade);
}
