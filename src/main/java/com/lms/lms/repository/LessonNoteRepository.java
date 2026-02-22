package com.lms.lms.repository;

import com.lms.lms.entity.LessonNote;
import com.lms.lms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LessonNoteRepository — handles database operations for lesson notes.
 *
 * Custom query: findBySubject()
 * - Students can filter lesson notes by subject to find relevant materials.
 */
@Repository
public interface LessonNoteRepository extends JpaRepository<LessonNote, Long> {

    /**
     * Find all lesson notes for a specific subject.
     * Spring Data JPA auto-generates: SELECT * FROM lesson_notes WHERE subject_id =
     * ?
     */
    List<LessonNote> findBySubject(Subject subject);
}
