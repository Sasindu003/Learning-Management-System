package com.lms.lms.service;

import com.lms.lms.entity.Role;
import com.lms.lms.entity.User;
import com.lms.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService — business logic for user management.
 *
 * WHY a service layer?
 * - Controllers should not talk to repositories directly.
 * - Services contain business logic (e.g., hashing passwords before saving).
 * - This separation makes the code easier to test and maintain.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Get all users — used by admin to list all users */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Find user by username — used for profile lookups and auth */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** Find user by ID */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Create a new user with an encrypted password.
     *
     * WHY encode the password here?
     * - Passwords must NEVER be stored as plain text.
     * - BCrypt hashing is one-way: even if the database is compromised, passwords
     * are safe.
     * - Spring Security's PasswordEncoder handles this for us.
     */
    public User createUser(String username, String password, String fullName, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hash the password!
        user.setFullName(fullName);
        user.setRole(role);
        return userRepository.save(user);
    }

    /** Check if a username is already taken */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /** Delete a user by ID — used by admin */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Toggle a user's active status (active ↔ suspended).
     * When suspended, the user cannot log in (blocked by CustomUserDetailsService).
     */
    public void toggleUserStatus(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(!user.isActive());
            userRepository.save(user);
        });
    }

    /** Save an existing user — used when updating user data */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
