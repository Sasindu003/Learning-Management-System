package com.lms.lms.repository;

import com.lms.lms.model.StudyNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyNoteRepository extends JpaRepository<StudyNote, Long> {
    Optional<StudyNote> findByUserIdAndCourseId(Long userId, Long courseId);
}
