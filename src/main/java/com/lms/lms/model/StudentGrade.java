package com.lms.lms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private String term; // Term 1, Term 2, etc.
    private String examName;
    private double marks;
    private double maxMarks;
    private double percentage;
    private String letterGrade; // A+, A, B+, B, etc.

    @Column(length = 500)
    private String remarks;

    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
        calculatePercentage();
        calculateLetterGrade();
    }

    public void calculatePercentage() {
        if (maxMarks > 0) {
            this.percentage = (marks / maxMarks) * 100;
        }
    }

    public void calculateLetterGrade() {
        if (percentage >= 90)
            letterGrade = "A+";
        else if (percentage >= 80)
            letterGrade = "A";
        else if (percentage >= 70)
            letterGrade = "B+";
        else if (percentage >= 60)
            letterGrade = "B";
        else if (percentage >= 50)
            letterGrade = "C+";
        else if (percentage >= 40)
            letterGrade = "C";
        else
            letterGrade = "F";
    }
}
