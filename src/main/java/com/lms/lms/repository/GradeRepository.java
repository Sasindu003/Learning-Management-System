package com.lms.lms.repository;

import com.lms.lms.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * GradeRepository — handles database operations for grades.
 *
 * No custom methods needed here — JpaRepository's built-in findAll(), save(),
 * deleteById() are enough for our CRUD operations.
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    // JpaRepository provides: save(), findById(), findAll(), deleteById(), count(),
    // etc.
}
