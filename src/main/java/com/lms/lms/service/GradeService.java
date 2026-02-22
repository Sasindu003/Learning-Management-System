package com.lms.lms.service;

import com.lms.lms.entity.Grade;
import com.lms.lms.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * GradeService — business logic for managing grade levels.
 *
 * This is a simple CRUD service. For now, it just wraps the repository.
 * As the app grows, business rules (e.g., "can't delete a grade that has
 * subjects")
 * would go here instead of in the controller.
 */
@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    /** Get all grades — used to populate dropdowns and grade lists */
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    /** Find a grade by ID */
    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }

    /** Save a new grade or update an existing one */
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    /** Delete a grade by ID */
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }
}
