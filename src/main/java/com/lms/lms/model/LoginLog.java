package com.lms.lms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String username;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime timestamp;

    private boolean success;

    private String failureReason;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
