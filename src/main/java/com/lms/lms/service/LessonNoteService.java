package com.lms.lms.service;

import com.lms.lms.entity.LessonNote;
import com.lms.lms.entity.Subject;
import com.lms.lms.repository.LessonNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * LessonNoteService — business logic for lesson note management.
 *
 * Note: File saving (to disk) is handled in the controller layer for
 * simplicity.
 * This service only deals with the database record.
 * In a larger app, you'd move file handling here too.
 */
@Service
public class LessonNoteService {

    private final LessonNoteRepository lessonNoteRepository;

    public LessonNoteService(LessonNoteRepository lessonNoteRepository) {
        this.lessonNoteRepository = lessonNoteRepository;
    }

    /** Get all lesson notes */
    public List<LessonNote> getAllLessonNotes() {
        return lessonNoteRepository.findAll();
    }

    /** Get lesson notes filtered by subject */
    public List<LessonNote> getLessonNotesBySubject(Subject subject) {
        return lessonNoteRepository.findBySubject(subject);
    }

    /** Find a lesson note by ID */
    public Optional<LessonNote> findById(Long id) {
        return lessonNoteRepository.findById(id);
    }

    /** Save a lesson note record to the database */
    public LessonNote saveLessonNote(LessonNote lessonNote) {
        return lessonNoteRepository.save(lessonNote);
    }

    /** Delete a lesson note by ID */
    public void deleteLessonNote(Long id) {
        lessonNoteRepository.deleteById(id);
    }
}
