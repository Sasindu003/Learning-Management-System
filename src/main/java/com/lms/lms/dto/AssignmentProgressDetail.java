package com.lms.lms.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentProgressDetail {
    private String title;
    private LocalDateTime dueDate;
    private boolean submitted;
    private LocalDateTime submittedAt;
    private Integer marks;
    private boolean graded;
    
    public String getStatus() {
        if (submitted) return "Completed";
        if (dueDate != null && LocalDateTime.now().isAfter(dueDate)) return "Overdue";
        return "Pending";
    }
}
