package com.lms.lms.service;

import com.lms.lms.entity.User;
import com.lms.lms.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService — the bridge between our User entity and Spring
 * Security.
 *
 * WHY do we need this?
 * - Spring Security doesn't know about our User entity or our database.
 * - It expects a UserDetailsService that can load user info by username.
 * - This class translates our User into Spring Security's UserDetails format.
 *
 * HOW it works:
 * 1. User types username + password on login page.
 * 2. Spring Security calls loadUserByUsername(username).
 * 3. We fetch our User from the DB and wrap it in a Spring Security User
 * object.
 * 4. Spring Security compares the password hash automatically.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection — Spring automatically injects UserRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username for Spring Security authentication.
     *
     * @param username the username typed on the login form
     * @return UserDetails object that Spring Security uses for authentication
     * @throws UsernameNotFoundException if no user exists with that username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Step 1: Look up the user in our database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        // Step 2: Convert our User into Spring Security's UserDetails format.
        // "ROLE_" prefix is required by Spring Security for role-based authorization.
        // So our Role.ADMIN becomes "ROLE_ADMIN", which matches hasRole("ADMIN")
        // checks.
        //
        // The 'user.isActive()' flag controls whether the account is enabled.
        // If an admin suspends a user (active = false), Spring Security will
        // reject their login automatically and show "Account is disabled".
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(), // enabled — false = suspended, can't log in
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
