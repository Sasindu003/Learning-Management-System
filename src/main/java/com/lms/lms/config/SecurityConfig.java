package com.lms.lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Value("${app.security.remember-me-key:lms-default-key}")
        private String rememberMeKey;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
                return new HttpSessionEventPublisher();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**",
                                                                 "/favicon.ico")
                                                .permitAll()
                                                .requestMatchers("/h2-console/**").hasRole("ADMIN")
                                                .requestMatchers("/files/**").authenticated()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/teacher/**").hasRole("TEACHER")
                                                .requestMatchers("/student/**").hasRole("STUDENT")
                                                .requestMatchers("/login", "/error", "/changelog", "/dev/**", "/portfolio/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .rememberMe(remember -> remember
                                                .key(rememberMeKey)
                                                .tokenValiditySeconds(86400))
                                .sessionManagement(session -> session
                                                .maximumSessions(5)
                                                .maxSessionsPreventsLogin(false)
                                                .sessionRegistry(sessionRegistry()))
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**", "/api/**"))
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        if (request.getRequestURI().startsWith("/admin")) {
                                                                response.sendRedirect("/login?logout=true");
                                                        } else {
                                                                response.sendRedirect("/dashboard");
                                                        }
                                                }))
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }
}
