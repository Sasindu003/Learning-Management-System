package com.lms.lms.config;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
            // Create default admin
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@eduflow.lms")
                    .role(Role.ADMIN)
                    .primaryId("AD001")
                    .enabled(true)
                    .build();
            userRepository.save(admin);

            // Create sample grades
            String[] gradeNames = { "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
                    "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10",
                    "Grade 11", "Grade 12" };
            for (String name : gradeNames) {
                gradeRepository.save(Grade.builder().name(name).description(name + " class").build());
            }

            // Create sample subjects
            String[][] subjects = {
                    { "Mathematics", "MATH", "Core mathematics" },
                    { "English", "ENG", "English language and literature" },
                    { "Science", "SCI", "General science" },
                    { "Physics", "PHY", "Physics" },
                    { "Chemistry", "CHEM", "Chemistry" },
                    { "Biology", "BIO", "Biology" },
                    { "History", "HIST", "World and local history" },
                    { "Geography", "GEO", "Physical and human geography" },
                    { "Computer Science", "CS", "Computer science and programming" },
                    { "Art", "ART", "Visual arts" }
            };
            for (String[] s : subjects) {
                subjectRepository.save(Subject.builder().name(s[0]).code(s[1]).description(s[2]).build());
            }

            // Create sample teacher
            User teacher = User.builder()
                    .username("teacher1")
                    .password(passwordEncoder.encode("teacher123"))
                    .fullName("John Smith")
                    .email("john.smith@eduflow.lms")
                    .role(Role.TEACHER)
                    .primaryId("TE001")
                    .enabled(true)
                    .build();
            userRepository.save(teacher);

            // Create sample student
            User student = User.builder()
                    .username("student1")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Jane Doe")
                    .email("jane.doe@eduflow.lms")
                    .role(Role.STUDENT)
                    .primaryId("ST001")
                    .enabled(true)
                    .grade(gradeRepository.findByName("Grade 10").orElse(null))
                    .build();
            userRepository.save(student);

            System.out.println("=== EduFlow LMS Initialized ===");
            System.out.println("Admin: admin / admin123");
            System.out.println("Teacher: teacher1 / teacher123");
            System.out.println("Student: student1 / student123");
        }
    }
}
