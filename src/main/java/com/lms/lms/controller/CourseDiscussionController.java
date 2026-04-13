package com.lms.lms.controller;

import com.lms.lms.model.Course;
import com.lms.lms.model.CourseDiscussion;
import com.lms.lms.model.User;
import com.lms.lms.service.CourseDiscussionService;
import com.lms.lms.service.CourseService;
import com.lms.lms.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CourseDiscussionController {

    private final CourseDiscussionService discussionService;
    private final CourseService courseService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/courses/{courseId}/discussions")
    public List<Map<String, Object>> getDiscussions(@PathVariable Long courseId, Authentication auth) {
        if (auth == null) return List.of();
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(courseId).orElseThrow();
        
        if (!discussionService.isUserAllowedInCourse(user, course)) {
            return List.of(); // Not authorized
        }

        return discussionService.getDiscussionsByCourse(courseId).stream().map(this::mapToResponse).toList();
    }

    @GetMapping("/api/discussions/recent")
    public List<Map<String, Object>> getRecentDiscussions(Authentication auth) {
        if (auth == null) return List.of();
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        List<CourseDiscussion> recent = discussionService.getRecentDiscussionsForUser(user, 5);
        return recent.stream().map(d -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", d.getId());
            map.put("courseId", d.getCourse().getId());
            map.put("courseTitle", d.getCourse().getTitle());
            map.put("senderName", d.getSender().getFullName());
            map.put("content", d.getContent().length() > 50 ? d.getContent().substring(0, 47) + "..." : d.getContent());
            map.put("createdAt", d.getCreatedAt());
            return map;
        }).toList();
    }

    @MessageMapping("/course/{courseId}/chat")
    public void handleChatMessage(@DestinationVariable Long courseId, @Payload ChatMessagePayload payload, Authentication auth) {
        if (auth == null) return;
        User sender = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(courseId).orElseThrow();

        if (!discussionService.isUserAllowedInCourse(sender, course)) {
            return; // Not authorized
        }

        CourseDiscussion discussion = CourseDiscussion.builder()
                .course(course)
                .sender(sender)
                .content(payload.getContent())
                .build();

        CourseDiscussion saved = discussionService.save(discussion);

        // Broadcast to all subscribers of this course
        messagingTemplate.convertAndSend("/topic/course/" + courseId, (Object) Map.of(
            "type", "NEW_MESSAGE",
            "message", mapToResponse(saved)
        ));
    }

    @DeleteMapping("/api/discussions/{id}")
    public void deleteMessage(@PathVariable Long id, Authentication auth) {
        CourseDiscussion discussion = discussionService.findById(id).orElseThrow();
        User user = userService.findByUsername(auth.getName()).orElseThrow();

        // Security check: Only sender or high-ranking roles can delete
        if (!discussion.getSender().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.TEACHER) {
            throw new RuntimeException("Unauthorized");
        }

        discussionService.delete(id);

        messagingTemplate.convertAndSend("/topic/course/" + discussion.getCourse().getId(), (Object) Map.of(
            "type", "DELETE_MESSAGE",
            "messageId", id
        ));
    }

    @PatchMapping("/api/discussions/{id}")
    public void updateMessage(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        CourseDiscussion discussion = discussionService.findById(id).orElseThrow();
        User user = userService.findByUsername(auth.getName()).orElseThrow();

        if (!discussion.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        discussion.setContent(body.get("content"));
        discussionService.save(discussion);

        messagingTemplate.convertAndSend("/topic/course/" + discussion.getCourse().getId(), (Object) Map.of(
            "type", "UPDATE_MESSAGE",
            "message", Map.of(
                "id", id,
                "content", discussion.getContent()
            )
        ));
    }

    private Map<String, Object> mapToResponse(CourseDiscussion d) {
        return Map.of(
            "id", d.getId(),
            "senderId", d.getSender().getId(),
            "senderName", d.getSender().getFullName(),
            "senderRole", d.getSender().getRole().toString(),
            "content", d.getContent(),
            "createdAt", d.getCreatedAt() != null ? d.getCreatedAt().toString() : ""
        );
    }

    @Data
    public static class ChatMessagePayload {
        private String content;
    }
}
