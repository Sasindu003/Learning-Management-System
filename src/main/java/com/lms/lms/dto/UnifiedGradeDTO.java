package com.lms.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedGradeDTO {
    private String type; // "Assignment", "Quiz", "Manual"
    private String subjectName;
    private String courseTitle;
    private String itemName; // e.g. "Midterm Exam", "Homework 1"
    private double marks;
    private double maxMarks;
    private double percentage;
    private String letterGrade;
    private String term;
    private String remarks;
    private LocalDateTime date;
    
    public static String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C+";
        if (percentage >= 40) return "C";
        return "F";
    }
}
