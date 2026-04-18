package com.lms.lms.service;

import com.lms.lms.model.Course;
import com.lms.lms.model.StudyNote;
import com.lms.lms.model.User;
import com.lms.lms.repository.StudyNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyNoteService {

    private final StudyNoteRepository studyNoteRepository;

    public Optional<StudyNote> getNote(User user, Course course) {
        return studyNoteRepository.findByUserIdAndCourseId(user.getId(), course.getId());
    }

    @Transactional
    public StudyNote saveNote(User user, Course course, String content) {
        StudyNote note = studyNoteRepository.findByUserIdAndCourseId(user.getId(), course.getId())
                .orElse(StudyNote.builder()
                        .user(user)
                        .course(course)
                        .build());
        
        note.setContent(content);
        note.setUpdatedAt(LocalDateTime.now());
        return studyNoteRepository.save(note);
    }
}
