package com.lms.lms.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherActivityDTO {
    private String studentName;
    private String studentPrimaryId;
    private String activityType; // "SUBMISSION", "EXAM_ATTEMPT", "DISCUSSION"
    private String description;
    private LocalDateTime timestamp;
    private String targetUrl;
    private String courseName;
}
