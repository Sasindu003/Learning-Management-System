package com.lms.lms.config;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // 1. Create default admin
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@eduflow.lms")
                    .role(Role.ADMIN)
                    .primaryId("AD001")
                    .enabled(true)
                    .build());

            // 2. Create exactly 5 grades
            String[] gradeNames = { "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5" };
            List<Grade> savedGrades = new ArrayList<>();
            for (String name : gradeNames) {
                savedGrades.add(gradeRepository.save(Grade.builder().name(name).description(name).build()));
            }

            // 3. Create exactly 5 subjects
            String[][] subjects = {
                    { "Mathematics", "MATH", "Core mathematics" },
                    { "English", "ENG", "English language" },
                    { "Science", "SCI", "General science" },
                    { "History", "HIST", "History" },
                    { "ICT", "ICT", "Information tech" }
            };
            for (String[] s : subjects) {
                subjectRepository.save(Subject.builder().name(s[0]).code(s[1]).description(s[2]).build());
            }

            // 4. Create exactly 2 teachers
            userRepository.save(User.builder()
                    .username("teacher1")
                    .password(passwordEncoder.encode("teacher123"))
                    .fullName("John Smith")
                    .email("john.smith@eduflow.lms")
                    .role(Role.TEACHER)
                    .primaryId("TE001")
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .username("teacher2")
                    .password(passwordEncoder.encode("teacher223"))
                    .fullName("Alice Johnson")
                    .email("alice.j@eduflow.lms")
                    .role(Role.TEACHER)
                    .primaryId("TE002")
                    .enabled(true)
                    .build());

            // 5. Create exactly 2 students
            userRepository.save(User.builder()
                    .username("student1")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Jane Doe")
                    .email("jane.doe@eduflow.lms")
                    .role(Role.STUDENT)
                    .primaryId("ST001")
                    .enabled(true)
                    .grade(savedGrades.get(0)) // Grade 1
                    .build());

            userRepository.save(User.builder()
                    .username("student2")
                    .password(passwordEncoder.encode("student223"))
                    .fullName("Bob Wilson")
                    .email("bob.wilson@eduflow.lms")
                    .role(Role.STUDENT)
                    .primaryId("ST002")
                    .enabled(true)
                    .grade(savedGrades.get(1)) // Grade 2
                    .build());

            System.out.println("=== EduFlow LMS Reset and Seeded Successfully ===");
        }
    }
}
