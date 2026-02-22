package com.lms.lms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * User entity — represents anyone who logs into the LMS.
 *
 * WHY these fields?
 * - username: used for login (must be unique).
 * - password: stored as a BCrypt hash, never plain text.
 * - fullName: displayed on the UI (e.g., "Welcome, John!").
 * - role: determines what pages/features the user can access.
 *
 * Lombok annotations save us from writing boilerplate getters, setters, and
 * constructors.
 */
@Entity
@Table(name = "users") // "user" is a reserved word in some DBs, so we use "users"
@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // JPA requires a no-arg constructor
@AllArgsConstructor // Convenient for creating users in code (e.g., data seeder)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false, unique = true) // Username must be unique and not null
    private String username;

    @Column(nullable = false) // Password is required
    private String password;

    @Column(nullable = false)
    private String fullName;

    /**
     * EnumType.STRING stores the role as "STUDENT", "TEACHER", or "ADMIN" in the
     * DB.
     * If we used EnumType.ORDINAL, it would store 0, 1, 2 — which breaks if we
     * reorder the enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Whether this user account is active.
     * Suspended users (active = false) cannot log in.
     * Defaults to true — new accounts are active by default.
     */
    @Column(nullable = false)
    private boolean active = true;
}
