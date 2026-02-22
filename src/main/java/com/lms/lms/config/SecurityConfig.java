package com.lms.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig — configures how Spring Security protects our LMS.
 *
 * KEY CONCEPTS:
 * 1. URL Authorization: Which pages require which roles.
 * 2. Login/Logout: How the login form works and where users go after login.
 * 3. Password Encoding: BCrypt hashing for secure password storage.
 *
 * HOW IT WORKS:
 * - We expose a PasswordEncoder bean and a UserDetailsService bean
 * (CustomUserDetailsService).
 * - Spring Boot auto-configures a DaoAuthenticationProvider using these two
 * beans.
 * - We only need to define the SecurityFilterChain to set up URL rules and
 * login/logout.
 *
 * This is a SIMPLE security setup — no JWT tokens, no OAuth, just form-based
 * login
 * with role-based page access. Perfect for learning!
 */
@Configuration
@EnableWebSecurity // Tells Spring to use our security config instead of the default
public class SecurityConfig {

    /**
     * PasswordEncoder bean — used everywhere passwords are hashed or compared.
     * BCrypt is the industry standard for password hashing.
     *
     * Spring Boot will automatically wire this into the DaoAuthenticationProvider
     * along with our CustomUserDetailsService bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain — the heart of our security configuration.
     * This defines WHICH URLs are protected and WHO can access them.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // URL Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Public pages — anyone can access these (even without logging in)
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // Role-based access control:
                        // Only ADMIN users can access /admin/** URLs
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Only TEACHER users can access /teacher/** URLs
                        .requestMatchers("/teacher/**").hasRole("TEACHER")

                        // Only STUDENT users can access /student/** URLs
                        .requestMatchers("/student/**").hasRole("STUDENT")

                        // Everything else requires authentication (must be logged in)
                        .anyRequest().authenticated())

                // Login Form Configuration
                .formLogin(form -> form
                        .loginPage("/login") // Our custom login page URL
                        .defaultSuccessUrl("/dashboard", true) // Where to go after successful login
                        .failureUrl("/login?error=true") // Stay on login page with error message
                        .permitAll() // Anyone can see the login page
                )

                // Logout Configuration
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL to trigger logout
                        .logoutSuccessUrl("/login?logout=true") // Where to go after logout
                        .invalidateHttpSession(true) // Clear session data
                        .deleteCookies("JSESSIONID") // Remove session cookie
                        .permitAll());

        return http.build();
    }
}
