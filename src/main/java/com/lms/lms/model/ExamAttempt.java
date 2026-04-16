package com.lms.lms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "exam_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    private int score;
    private int totalMarks;
    private double percentage;

    @Builder.Default
    private boolean completed = false;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder.Default
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamAnswer> answers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        startTime = LocalDateTime.now();
    }
}
