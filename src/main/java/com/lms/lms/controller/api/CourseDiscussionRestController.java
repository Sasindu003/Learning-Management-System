package com.lms.lms.controller.api;

import com.lms.lms.dto.CourseActivityDTO;
import com.lms.lms.model.User;
import com.lms.lms.service.CourseDiscussionService;
import com.lms.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class CourseDiscussionRestController {

    private final CourseDiscussionService discussionService;
    private final UserService userService;

    @GetMapping("/recent")
    public ResponseEntity<List<CourseActivityDTO>> getRecentDiscussions(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        
        return userService.findByUsername(auth.getName())
                .map(user -> ResponseEntity.ok(discussionService.getRecentCourseActivities(user)))
                .orElse(ResponseEntity.status(401).build());
    }
}
