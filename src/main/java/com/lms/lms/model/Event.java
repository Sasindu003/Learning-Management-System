package com.lms.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Event title is required")
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    private String eventTime;

    @Enumerated(EnumType.STRING)
    private EventType type = EventType.GENERAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum EventType {
        GENERAL, HOLIDAY, EXAM, MEETING, SPORTS, CULTURAL
    }
}
