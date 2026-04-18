package com.lms.lms.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignmentProgress {
    private Long studentId;
    private int completedCount;
    private int totalCount;

    public String getDisplayProgress() {
        return completedCount + " / " + totalCount;
    }
}
