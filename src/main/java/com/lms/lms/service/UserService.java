package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPrimaryId(generatePrimaryId(user.getRole()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public String generatePrimaryId(Role role) {
        String prefix;
        switch (role) {
            case STUDENT:
                prefix = "ST";
                break;
            case TEACHER:
                prefix = "TE";
                break;
            case ADMIN:
                prefix = "AD";
                break;
            default:
                prefix = "US";
        }
        long count = userRepository.countByRole(role) + 1;
        String id;
        do {
            id = String.format("%s%03d", prefix, count);
            count++;
        } while (userRepository.existsByPrimaryId(id));
        return id;
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public List<User> findStudentsByGrade(Grade grade) {
        return userRepository.findByRoleAndGrade(Role.STUDENT, grade);
    }
}
