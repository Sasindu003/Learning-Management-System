package com.lms.lms.dto;

import java.time.LocalDateTime;

public record CourseActivityDTO(
    Long courseId,
    String courseName,
    String lastMessage,
    String senderName,
    LocalDateTime sentAt
) {}
