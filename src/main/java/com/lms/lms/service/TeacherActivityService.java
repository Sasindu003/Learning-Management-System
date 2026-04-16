package com.lms.lms.service;

import com.lms.lms.dto.TeacherActivityDTO;
import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherActivityService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CourseDiscussionRepository discussionRepository;

    public List<TeacherActivityDTO> getRecentActivities(User teacher) {
        if (teacher.getGrades() == null || teacher.getGrades().isEmpty()) {
            return new ArrayList<>();
        }

        int limit = 10;
        PageRequest pageRequest = PageRequest.of(0, limit);

        // 1. Submissions
        List<TeacherActivityDTO> submissions = submissionRepository
                .findRecentByTeacherAndGrades(teacher, teacher.getGrades(), pageRequest)
                .stream().map(s -> TeacherActivityDTO.builder()
                        .studentName(s.getStudent().getFullName())
                        .studentPrimaryId(s.getStudent().getPrimaryId())
                        .activityType("SUBMISSION")
                        .description("submitted assignment: " + s.getAssignment().getTitle())
                        .timestamp(s.getSubmittedAt())
                        .courseName(s.getAssignment().getCourse().getTitle())
                        .targetUrl("/teacher/assignments/" + s.getAssignment().getId() + "/submissions")
                        .build())
                .collect(Collectors.toList());

        // 2. Exam Attempts
        List<TeacherActivityDTO> attempts = examAttemptRepository
                .findRecentByTeacherAndGrades(teacher, teacher.getGrades(), pageRequest)
                .stream().map(e -> TeacherActivityDTO.builder()
                        .studentName(e.getStudent().getFullName())
                        .studentPrimaryId(e.getStudent().getPrimaryId())
                        .activityType("EXAM_ATTEMPT")
                        .description("completed exam: " + e.getExam().getTitle() + " (Score: " + e.getScore() + "/" + e.getExam().getTotalMarks() + ")")
                        .timestamp(e.getEndTime())
                        .courseName(e.getExam().getCourse().getTitle())
                        .targetUrl("/teacher/exams/" + e.getExam().getId() + "/results")
                        .build())
                .collect(Collectors.toList());

        // 3. Discussion Posts
        List<TeacherActivityDTO> discussions = discussionRepository
                .findRecentByTeacherAndGrades(teacher, teacher.getGrades(), pageRequest)
                .stream().map(d -> TeacherActivityDTO.builder()
                        .studentName(d.getSender().getFullName())
                        .studentPrimaryId(d.getSender().getPrimaryId())
                        .activityType("DISCUSSION")
                        .description("posted in discussion: " + (d.getContent().length() > 50 ? d.getContent().substring(0, 47) + "..." : d.getContent()))
                        .timestamp(d.getCreatedAt())
                        .courseName(d.getCourse().getTitle())
                        .targetUrl("/teacher/courses/" + d.getCourse().getId())
                        .build())
                .collect(Collectors.toList());

        // Aggregate, sort and limit
        List<TeacherActivityDTO> allActivities = new ArrayList<>();
        allActivities.addAll(submissions);
        allActivities.addAll(attempts);
        allActivities.addAll(discussions);

        return allActivities.stream()
                .sorted(Comparator.comparing(TeacherActivityDTO::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
