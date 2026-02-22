package com.lms.lms.service;

import com.lms.lms.entity.Grade;
import com.lms.lms.entity.Subject;
import com.lms.lms.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SubjectService — business logic for managing subjects.
 *
 * Subjects are tied to grades, so we have a method to filter by grade.
 */
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    /** Get all subjects — used for dropdowns when creating lessons/assignments */
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    /** Get subjects filtered by grade — e.g., "all subjects in Grade 10" */
    public List<Subject> getSubjectsByGrade(Grade grade) {
        return subjectRepository.findByGrade(grade);
    }

    /** Find a subject by ID */
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    /** Save a new subject or update an existing one */
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    /** Delete a subject by ID */
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}
