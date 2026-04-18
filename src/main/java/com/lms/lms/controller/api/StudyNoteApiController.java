package com.lms.lms.controller.api;

import com.lms.lms.model.Course;
import com.lms.lms.model.StudyNote;
import com.lms.lms.model.User;
import com.lms.lms.service.CourseService;
import com.lms.lms.service.StudyNoteService;
import com.lms.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class StudyNoteApiController {

    private final StudyNoteService studyNoteService;
    private final UserService userService;
    private final CourseService courseService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getNote(@PathVariable Long courseId, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(courseId).orElseThrow();
        
        Optional<StudyNote> note = studyNoteService.getNote(user, course);
        return ResponseEntity.ok(note.map(StudyNote::getContent).orElse(""));
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<?> saveNote(@PathVariable Long courseId, @RequestBody Map<String, String> body, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(courseId).orElseThrow();
        String content = body.getOrDefault("content", "");
        
        studyNoteService.saveNote(user, course, content);
        return ResponseEntity.ok(Map.of("message", "Note saved successfully"));
    }
}
