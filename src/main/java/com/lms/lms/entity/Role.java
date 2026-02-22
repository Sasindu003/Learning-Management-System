package com.lms.lms.entity;

/**
 * Role enum — defines the three types of users in our LMS.
 *
 * WHY an enum?
 * - It's the simplest way to restrict user types to a fixed set.
 * - JPA stores this as a String in the database
 * (via @Enumerated(EnumType.STRING) on the User entity).
 * - Spring Security uses these values prefixed with "ROLE_" for authorization
 * checks.
 */
public enum Role {
    STUDENT, // Can view lessons, assignments, and announcements
    TEACHER, // Can upload lessons, create assignments
    ADMIN // Can manage grades, subjects, users, and post announcements
}
