package com.lms.lms.repository;

import com.lms.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — handles all database operations for the User entity.
 *
 * WHY extend JpaRepository?
 * - JpaRepository gives us CRUD methods for free: save(), findById(),
 * findAll(), delete(), etc.
 * - We only need to define custom query methods (Spring Data JPA auto-generates
 * the SQL).
 *
 * WHY Optional<User>?
 * - A user might not exist with the given username (e.g., wrong login).
 * - Optional forces us to handle the "not found" case explicitly, avoiding
 * NullPointerException.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username.
     * Spring Data JPA auto-generates: SELECT * FROM users WHERE username = ?
     * Used by: CustomUserDetailsService for login authentication.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username is already taken.
     * Used by: AdminController when creating new users to prevent duplicates.
     */
    boolean existsByUsername(String username);
}
