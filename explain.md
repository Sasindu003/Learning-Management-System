# 🎓 EduFlow LMS - Complete Project Visual Explanation

This document provides a visual breakdown of every function and feature in the Learning Management System for easy understanding by group members.

---

## 📁 COMPLETE CODE TREE

```
Learning-Management-System/
├── 📄 pom.xml                                    # Maven dependencies & build config
├── 📄 README.md                                  # Project documentation
├── 📄 system.properties                          # Deployment settings
├── 📄 Dockerfile                                 # Container config
│
├── 📁 data/                                      # H2 Database files
│   ├── lmsdb.mv.db
│   └── lmsdb.trace.db
│
├── 📁 uploads/                                   # User uploaded files
│   ├── assignments/                              # Assignment attachments
│   ├── materials/                                # Course materials
│   ├── profiles/                                 # Profile pictures
│   └── submissions/                              # Student submissions
│
├── 📁 logs/                                      # Application logs
│
├── 📁 settings/                                  # App settings
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/lms/lms/
│   │   │   │
│   │   │   ├── 📄 LmsApplication.java            # Application entry point
│   │   │   │
│   │   │   ├── 📁 component/
│   │   │   │   └── 📄 LoginListener.java         # Tracks user login events
│   │   │   │
│   │   │   ├── 📁 config/
│   │   │   │   ├── 📄 SecurityConfig.java        # Authentication & authorization rules
│   │   │   │   ├── 📄 WebSocketConfig.java       # Real-time messaging setup
│   │   │   │   ├── 📄 CloudflareR2Config.java    # Cloud storage configuration
│   │   │   │   ├── 📄 DataInitializer.java       # Initial data setup
│   │   │   │   └── 📄 UserStatusListener.java   # Online/offline status tracking
│   │   │   │
│   │   │   ├── 📁 controller/                    # HANDLES HTTP REQUESTS
│   │   │   │   ├── 📄 AuthController.java       # Login/logout & role-based routing
│   │   │   │   ├── 📄 AdminController.java      # Admin dashboard & management
│   │   │   │   ├── 📄 TeacherController.java    # Teacher portal features
│   │   │   │   ├── 📄 StudentController.java    # Student portal features
│   │   │   │   ├── 📄 ProfileController.java    # User profile management
│   │   │   │   ├── 📄 MessageController.java    # Private messaging system
│   │   │   │   ├── 📄 NotificationController.java  # User notifications
│   │   │   │   ├── 📄 CalendarController.java   # Calendar & events view
│   │   │   │   ├── 📄 CourseDiscussionController.java  # Discussion forums
│   │   │   │   ├── 📄 CourseActionController.java      # Course enrollment
│   │   │   │   ├── 📄 FileController.java       # File upload/download
│   │   │   │   ├── 📄 PortfolioController.java  # Teacher public profiles
│   │   │   │   ├── 📄 ChangelogController.java  # Version history
│   │   │   │   ├── 📄 DevController.java        # Development utilities
│   │   │   │   ├── 📄 UserStatusController.java # Online status API
│   │   │   │   └── 📁 api/                      # REST API controllers
│   │   │   │       ├── 📄 CourseDiscussionRestController.java
│   │   │   │       └── 📄 StudyNoteApiController.java
│   │   │   │
│   │   │   ├── 📁 service/                      # BUSINESS LOGIC LAYER
│   │   │   │   ├── 📄 UserService.java           # User CRUD + password handling
│   │   │   │   ├── 📄 CourseService.java         # Course management
│   │   │   │   ├── 📄 AssignmentService.java     # Assignment & submission handling
│   │   │   │   ├── 📄 ExamService.java           # Quiz/exam management
│   │   │   │   ├── 📄 GradeService.java          # Academic grade management
│   │   │   │   ├── 📄 SubjectService.java        # Subject management
│   │   │   │   ├── 📄 AttendanceService.java     # Attendance tracking
│   │   │   │   ├── 📄 NotificationService.java   # Notification system
│   │   │   │   ├── 📄 MessageService.java        # Message handling
│   │   │   │   ├── 📄 AnnouncementService.java   # Announcement management
│   │   │   │   ├── 📄 TimetableService.java      # Schedule management
│   │   │   │   ├── 📄 ActivityLogService.java    # Activity logging
│   │   │   │   ├── 📄 StudentGradeService.java   # Student grade calculations
│   │   │   │   ├── 📄 FileStorageService.java    # File upload/download logic
│   │   │   │   ├── 📄 CustomUserDetailsService.java  # Spring Security user loading
│   │   │   │   ├── 📄 AcademicTermService.java   # Academic terms
│   │   │   │   ├── 📄 EventService.java          # Calendar events
│   │   │   │   ├── 📄 LoginLogService.java       # Login history
│   │   │   │   └── 📄 CalendarEntryTypeService.java  # Event types
│   │   │   │
│   │   │   ├── 📁 repository/                   # DATABASE ACCESS LAYER
│   │   │   │   ├── 📄 UserRepository.java
│   │   │   │   ├── 📄 CourseRepository.java
│   │   │   │   ├── 📄 AssignmentRepository.java
│   │   │   │   ├── 📄 AssignmentSubmissionRepository.java
│   │   │   │   ├── 📄 ExamRepository.java
│   │   │   │   ├── 📄 ExamQuestionRepository.java
│   │   │   │   ├── 📄 ExamAttemptRepository.java
│   │   │   │   ├── 📄 GradeRepository.java
│   │   │   │   ├── 📄 SubjectRepository.java
│   │   │   │   ├── 📄 AttendanceRepository.java
│   │   │   │   ├── 📄 AnnouncementRepository.java
│   │   │   │   ├── 📄 NotificationRepository.java
│   │   │   │   ├── 📄 MessageRepository.java
│   │   │   │   ├── 📄 TimetableRepository.java
│   │   │   │   ├── 📄 ActivityLogRepository.java
│   │   │   │   └── 📄 [10 more repositories...]
│   │   │   │
│   │   │   ├── 📁 model/                        # ENTITY CLASSES (Database Tables)
│   │   │   │   ├── 📄 User.java                  # User entity (Student/Teacher/Admin)
│   │   │   │   ├── 📄 Course.java               # Course entity
│   │   │   │   ├── 📄 Grade.java                # Academic grade/level
│   │   │   │   ├── 📄 Subject.java              # Subject entity
│   │   │   │   ├── 📄 Assignment.java            # Assignment entity
│   │   │   │   ├── 📄 AssignmentSubmission.java  # Student submission
│   │   │   │   ├── 📄 Exam.java                 # Quiz/Exam entity
│   │   │   │   ├── 📄 ExamQuestion.java         # Exam questions
│   │   │   │   ├── 📄 ExamAttempt.java          # Student exam attempts
│   │   │   │   ├── 📄 Attendance.java            # Attendance records
│   │   │   │   ├── 📄 Announcement.java          # System announcements
│   │   │   │   ├── 📄 Notification.java          # User notifications
│   │   │   │   ├── 📄 Message.java               # Private messages
│   │   │   │   ├── 📄 Timetable.java              # Schedule entries
│   │   │   │   ├── 📄 CourseMaterial.java        # Course files
│   │   │   │   ├── 📄 CourseDiscussion.java      # Discussion posts
│   │   │   │   ├── 📄 ActivityLog.java           # System activity log
│   │   │   │   └── 📄 [10 more models...]
│   │   │   │
│   │   │   └── 📁 dto/                          # DATA TRANSFER OBJECTS
│   │   │       ├── 📄 UnifiedGradeDTO.java
│   │   │       ├── 📄 CourseActivityDTO.java
│   │   │       ├── 📄 AssignmentProgressDetail.java
│   │   │       └── 📄 StudentAssignmentProgress.java
│   │   │
│   │   └── 📁 resources/
│   │       ├── 📄 application.properties        # App configuration
│   │       ├── 📁 static/                       # Static web assets
│   │       │   ├── 📁 css/style.css
│   │       │   ├── 📁 js/app.js
│   │       │   └── 📁 images/
│   │       │
│   │       └── 📁 templates/                    # Thymeleaf HTML templates
│   │           ├── 📄 login.html
│   │           ├── 📄 changelog.html
│   │           ├── 📁 admin/                    # Admin UI pages
│   │           │   ├── dashboard.html
│   │           │   ├── users.html
│   │           │   ├── courses.html
│   │           │   ├── grades.html
│   │           │   ├── subjects.html
│   │           │   ├── announcements.html
│   │           │   ├── events.html
│   │           │   ├── timetable.html
│   │           │   ├── terms.html
│   │           │   └── login-history.html
│   │           │
│   │           ├── 📁 teacher/                  # Teacher UI pages
│   │           │   ├── dashboard.html
│   │           │   ├── courses.html
│   │           │   ├── course-detail.html
│   │           │   ├── assignments.html
│   │           │   ├── exams.html
│   │           │   ├── attendance.html
│   │           │   ├── grades.html
│   │           │   └── [more templates...]
│   │           │
│   │           ├── 📁 student/                  # Student UI pages
│   │           │   ├── dashboard.html
│   │           │   ├── courses.html
│   │           │   ├── course-detail.html
│   │           │   ├── assignments.html
│   │           │   ├── quizzes.html
│   │           │   ├── grades.html
│   │           │   ├── attendance.html
│   │           │   └── [more templates...]
│   │           │
│   │           ├── 📁 profile/                  # Profile pages
│   │           ├── 📁 messages/                 # Messaging UI
│   │           ├── 📁 fragments/                # Shared layout components
│   │           │   └── layout.html              # Main layout template
│   │           │
│   │           └── 📁 portfolio.html
│   │
│   └── 📁 test/                                 # Unit & integration tests
│
└── 📁 .git/                                     # Git version control
```

---

## 🏗️ ARCHITECTURE LAYERS EXPLAINED

The application follows a **Layered Architecture** pattern with 4 main layers:

```
┌─────────────────────────────────────────────────────────────┐
│  🌐 PRESENTATION LAYER (Controllers + Templates)           │
│  - Handles HTTP requests                                    │
│  - Renders HTML pages                                       │
│  - REST API endpoints                                       │
├─────────────────────────────────────────────────────────────┤
│  ⚙️ BUSINESS LOGIC LAYER (Services)                          │
│  - Contains business rules                                  │
│  - Orchestrates data operations                             │
│  - Transaction management                                   │
├─────────────────────────────────────────────────────────────┤
│  💾 DATA ACCESS LAYER (Repositories)                         │
│  - Database queries                                         │
│  - CRUD operations                                          │
│  - Extends Spring Data JPA                                  │
├─────────────────────────────────────────────────────────────┤
│  🗄️ DATABASE LAYER (Models/Entities)                         │
│  - Entity definitions (JPA annotations)                     │
│  - Database schema mapping                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 SECURITY & AUTHENTICATION FLOW

```
┌──────────────────────────────────────────────────────────────┐
│                    LOGIN FLOW                                │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  1. User enters credentials at /login                        │
│           ↓                                                  │
│  2. SecurityConfig intercepts request                      │
│           ↓                                                  │
│  3. CustomUserDetailsService.loadUserByUsername()           │
│           ↓                                                  │
│  4. Validates password with BCryptPasswordEncoder            │
│           ↓                                                  │
│  5. Creates Authentication object with ROLE_*                 │
│           ↓                                                  │
│  6. AuthController.dashboard() redirects based on role:       │
│     • ADMIN → /admin/dashboard                               │
│     • TEACHER → /teacher/dashboard                           │
│     • STUDENT → /student/dashboard                           │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### SecurityConfig.java - Authorization Rules

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // URL Access Rules
            .authorizeHttpRequests(auth -> auth
                // Public: Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // Public: Login, error pages
                .requestMatchers("/login", "/error", "/changelog").permitAll()
                
                // Role-based access
                .requestMatchers("/admin/**").hasRole("ADMIN")       // Only admins
                .requestMatchers("/teacher/**").hasRole("TEACHER") // Only teachers
                .requestMatchers("/student/**").hasRole("STUDENT") // Only students
                
                // Everything else requires login
                .anyRequest().authenticated()
            )
            
            // Login form configuration
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
            )
            
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
            )
            
            // Remember me feature
            .rememberMe(remember -> remember
                .key("lms-secret-key")
                .tokenValiditySeconds(86400)  // 24 hours
            );
        
        return http.build();
    }
}
```

---

## 👤 USER MANAGEMENT SYSTEM

### User Entity (Database Table Structure)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Primary key
    
    private String primaryId;           // ST001, TE001, AD001 (display ID)
    private String username;            // Login username (unique)
    private String password;            // BCrypt encrypted password
    private String fullName;            // Display name
    private String email;               // Contact email
    private String phone;               // Phone number
    
    @Enumerated(EnumType.STRING)
    private Role role;                  // ADMIN, TEACHER, STUDENT
    
    private boolean enabled = true;     // Account active status
    private String profilePicture;      // Avatar image path
    private String address;             // Physical address
    private String bio;                 // Profile biography
    
    // Relationships
    @ManyToOne
    private Grade grade;              // Student's grade/class
    
    @ManyToMany
    private Set<Grade> grades;          // Teacher's assigned grades
    
    @ManyToMany
    private Set<Subject> subjects;      // Teacher's subjects
    
    @ManyToMany
    private Set<Course> pinnedCourses;  // Pinned courses for quick access
}
```

### UserService.java - Key Functions

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // CREATE USER
    @Transactional
    public User createUser(User user) {
        // 1. Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 2. Generate unique ID (ST001, TE001, etc.)
        user.setPrimaryId(generatePrimaryId(user.getRole()));
        
        // 3. Save to database
        return userRepository.save(user);
    }
    
    // UPDATE USER
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    // TOGGLE ACCOUNT STATUS (Enable/Disable)
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setEnabled(!user.isEnabled());  // Flip status
        userRepository.save(user);
    }
    
    // GENERATE PRIMARY ID
    public String generatePrimaryId(Role role) {
        String prefix = switch(role) {
            case STUDENT -> "ST";   // ST001, ST002...
            case TEACHER -> "TE";   // TE001, TE002...
            case ADMIN -> "AD";     // AD001, AD002...
        };
        
        long count = userRepository.countByRole(role) + 1;
        return String.format("%s%03d", prefix, count);  // e.g., ST001
    }
    
    // FIND USERS
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    // PIN/UNPIN COURSE
    @Transactional
    public void toggleCoursePin(User user, Course course) {
        if (user.getPinnedCourses().contains(course)) {
            user.getPinnedCourses().remove(course);
        } else {
            user.getPinnedCourses().add(course);
        }
        userRepository.save(user);
    }
}
```

---

## 📚 COURSE MANAGEMENT SYSTEM

### Course Entity Structure

```java
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;               // Course name
    private String description;         // Course description
    
    @ManyToOne
    private Subject subject;            // e.g., Mathematics
    
    @ManyToOne
    private Grade grade;              // e.g., Grade 10
    
    @ManyToOne
    private User teacher;             // Assigned teacher
    
    @OneToMany(mappedBy = "course")
    private List<CourseMaterial> materials = new ArrayList<>();
    
    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments = new ArrayList<>();
    
    @OneToMany(mappedBy = "course")
    private List<Exam> exams = new ArrayList<>();
    
    private boolean active = true;      // Course status
}
```

### CourseService.java - Key Operations

```java
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMaterialRepository materialRepository;
    private final FileStorageService fileStorageService;
    
    // FIND COURSES
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }
    
    // Find courses by teacher
    public List<Course> findByTeacher(User teacher) {
        return courseRepository.findByTeacher(teacher);
    }
    
    // Find courses by grade (for students)
    public List<Course> findByGrade(Grade grade) {
        return courseRepository.findByGradeAndActive(grade, true);
    }
    
    // SAVE/UPDATE COURSE
    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }
    
    // DELETE COURSE (with cascade cleanup)
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Collect all files to delete
        List<String> filesToDelete = new ArrayList<>();
        
        // 1. Delete material files
        for (CourseMaterial material : course.getMaterials()) {
            if (material.getFilePath() != null) {
                filesToDelete.add(material.getFilePath());
            }
        }
        
        // 2. Delete assignment attachments & submissions
        for (Assignment assignment : course.getAssignments()) {
            if (assignment.getAttachmentPath() != null) {
                filesToDelete.add(assignment.getAttachmentPath());
            }
            for (AssignmentSubmission sub : assignment.getSubmissions()) {
                if (sub.getFilePath() != null) {
                    filesToDelete.add(sub.getFilePath());
                }
            }
        }
        
        // 3. Delete from database (cascade removes all related data)
        courseRepository.delete(course);
        
        // 4. Delete physical files
        for (String path : filesToDelete) {
            fileStorageService.delete(path);
        }
    }
    
    // MATERIAL MANAGEMENT
    public List<CourseMaterial> getMaterials(Course course) {
        return materialRepository.findByCourseOrderByUploadedAtDesc(course);
    }
    
    @Transactional
    public CourseMaterial saveMaterial(CourseMaterial material) {
        return materialRepository.save(material);
    }
    
    @Transactional
    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }
}
```

---

## 📝 ASSIGNMENT SYSTEM

### Assignment Entity

```java
@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;               // Assignment name
    private String description;         // Instructions
    private LocalDateTime dueDate;      // Deadline
    private int maxMarks = 100;         // Maximum points
    private String attachmentPath;      // Teacher's attachment file
    private String attachmentName;      // Original filename
    
    @ManyToOne
    private Course course;              // Parent course
    
    @OneToMany(mappedBy = "assignment")
    private List<AssignmentSubmission> submissions = new ArrayList<>();
    
    // Helper method
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate);
    }
}
```

### AssignmentSubmission Entity

```java
@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Assignment assignment;      // Parent assignment
    
    @ManyToOne
    private User student;               // Submitter
    
    private String filePath;              // Uploaded file location
    private String fileName;              // Original filename
    private String comments;              // Student's comments
    
    // Grading fields
    private Integer marks;                // Score received
    private String feedback;              // Teacher feedback
    private boolean graded = false;       // Grading status
    private LocalDateTime submittedAt;  // Submission time
    private LocalDateTime gradedAt;       // Grading time
}
```

### AssignmentService.java - Core Operations

```java
@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final ActivityLogService activityLogService;
    
    // FIND ASSIGNMENTS
    public List<Assignment> findByCourse(Course course) {
        return assignmentRepository.findByCourseOrderByDueDateDesc(course);
    }
    
    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }
    
    // CREATE/UPDATE ASSIGNMENT
    @Transactional
    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
    
    // DELETE ASSIGNMENT
    @Transactional
    public void delete(Long id) {
        assignmentRepository.deleteById(id);
    }
    
    // SUBMISSION OPERATIONS
    
    // Check if student has submitted
    public boolean hasSubmitted(Assignment assignment, User student) {
        return submissionRepository.existsByAssignmentAndStudent(assignment, student);
    }
    
    // Get student's submission
    public Optional<AssignmentSubmission> getSubmission(Assignment assignment, User student) {
        return submissionRepository.findLatestByAssignmentAndStudent(assignment, student);
    }
    
    // Submit assignment (create or update)
    @Transactional
    public AssignmentSubmission submitAssignment(AssignmentSubmission submission) {
        AssignmentSubmission saved = submissionRepository.save(submission);
        
        // Log activity for teacher notification
        activityLogService.log(
            saved.getStudent(),
            saved.getAssignment().getCourse(),
            ActivityLogType.ASSIGNMENT_SUBMISSION,
            saved.getStudent().getFullName() + " submitted: " + saved.getAssignment().getTitle(),
            "/teacher/assignments/" + saved.getAssignment().getId() + "/submissions"
        );
        
        return saved;
    }
    
    // Grade submission
    @Transactional
    public AssignmentSubmission gradeSubmission(Long submissionId, int marks, String feedback) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        submission.setMarks(marks);
        submission.setFeedback(feedback);
        submission.setGraded(true);
        submission.setGradedAt(LocalDateTime.now());
        
        return submissionRepository.save(submission);
    }
}
```

---

## 🧪 EXAM/QUIZ SYSTEM

### Exam Entity

```java
@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;               // Exam name
    private String description;         // Instructions
    
    @Enumerated(EnumType.STRING)
    private ExamType type = ExamType.QUIZ;  // QUIZ, MIDTERM, FINAL
    
    private LocalDateTime examDate;     // Scheduled date
    private int durationMinutes = 30;   // Time limit
    private int totalMarks = 100;       // Total points
    
    @ManyToOne
    private Course course;              // Parent course
    
    @OneToMany(mappedBy = "exam")
    private List<ExamQuestion> questions = new ArrayList<>();
    
    @OneToMany(mappedBy = "exam")
    private List<ExamAttempt> attempts = new ArrayList<>();
    
    private boolean published = false;  // Visibility
    private boolean active = true;      // Status
}

public enum ExamType {
    QUIZ, MIDTERM, FINAL, ASSIGNMENT_QUIZ
}
```

### ExamQuestion Entity

```java
@Entity
@Table(name = "exam_questions")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Exam exam;                  // Parent exam
    
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;  // MULTIPLE_CHOICE, TRUE_FALSE
    
    private String questionText;        // Question content
    private String optionA, optionB, optionC, optionD;  // MCQ options
    private String correctAnswer;       // Correct answer letter
    private int marks;                  // Points for this question
    private int questionOrder;          // Display order
}
```

### ExamService.java - Quiz Operations

```java
@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamQuestionRepository questionRepository;
    private final ExamAttemptRepository attemptRepository;
    private final ExamAnswerRepository answerRepository;
    
    // FIND EXAMS
    public List<Exam> findByCourse(Course course) {
        return examRepository.findByCourseOrderByExamDateDesc(course);
    }
    
    // Find published exams for student view
    public List<Exam> findPublishedByCourses(List<Course> courses) {
        return examRepository.findByCourseInAndPublished(courses, true);
    }
    
    // CHECK IF STUDENT ATTEMPTED
    public boolean hasAttempted(Exam exam, User student) {
        return attemptRepository.existsByExamAndStudent(exam, student);
    }
    
    // START EXAM ATTEMPT
    @Transactional
    public ExamAttempt startAttempt(Exam exam, User student) {
        // Prevent multiple attempts
        if (attemptRepository.existsByExamAndStudent(exam, student)) {
            throw new RuntimeException("Already attempted this exam");
        }
        
        ExamAttempt attempt = ExamAttempt.builder()
            .exam(exam)
            .student(student)
            .totalMarks(exam.getTotalMarks())
            .build();
        
        return attemptRepository.save(attempt);
    }
    
    // SUBMIT EXAM & AUTO-GRADE
    @Transactional
    public ExamAttempt submitAttempt(Long attemptId, Map<Long, String> answers) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
            .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        int score = 0;
        List<ExamQuestion> questions = questionRepository
            .findByExamOrderByQuestionOrderAsc(attempt.getExam());
        
        // Grade each answer
        for (ExamQuestion question : questions) {
            String selected = answers.get(question.getId());
            boolean correct = selected != null && 
                selected.equalsIgnoreCase(question.getCorrectAnswer());
            
            // Save the answer
            ExamAnswer answer = ExamAnswer.builder()
                .attempt(attempt)
                .question(question)
                .selectedAnswer(selected)
                .correct(correct)
                .marksAwarded(correct ? question.getMarks() : 0)
                .build();
            answerRepository.save(answer);
            
            if (correct) score += question.getMarks();
        }
        
        // Update attempt
        attempt.setScore(score);
        attempt.setCompleted(true);
        attempt.setEndTime(LocalDateTime.now());
        attempt.setPercentage((double) score / attempt.getTotalMarks() * 100);
        
        ExamAttempt saved = attemptRepository.save(attempt);
        
        // Log activity
        activityLogService.log(
            saved.getStudent(),
            saved.getExam().getCourse(),
            ActivityLogType.EXAM_ATTEMPT,
            saved.getStudent().getFullName() + " completed " + saved.getExam().getType(),
            "/teacher/exams/" + saved.getExam().getId() + "/results"
        );
        
        return saved;
    }
}
```

---

## 📊 CONTROLLER BREAKDOWN BY ROLE

### 1. AdminController.java - Admin Functions

```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Show statistics
        model.addAttribute("studentCount", userService.countByRole(Role.STUDENT));
        model.addAttribute("teacherCount", userService.countByRole(Role.TEACHER));
        model.addAttribute("courseCount", courseService.findAll().size());
        model.addAttribute("gradeCount", gradeService.findAll().size());
        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        model.addAttribute("upcomingEvents", eventService.findUpcoming());
        return "admin/dashboard";
    }
    
    // ========== USER MANAGEMENT ==========
    // List all users
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("teachers", userService.findByRole(Role.TEACHER));
        model.addAttribute("students", userService.findByRole(Role.STUDENT));
        model.addAttribute("admins", userService.findByRole(Role.ADMIN));
        return "admin/users";
    }
    
    // Show create user form
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("roles", User.Role.values());
        return "admin/user-form";
    }
    
    // Create/Save user
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user,
            @RequestParam(value = "gradeId", required = false) Long gradeId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
            RedirectAttributes ra) {
        
        // Check duplicate username
        if (user.getId() == null && userService.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already exists!");
            return "redirect:/admin/users/new";
        }
        
        // Set grade for students
        if (gradeId != null) {
            user.setGrade(gradeService.findById(gradeId).orElse(null));
        }
        
        // Set multiple grades for teachers
        if (gradeIds != null) {
            Set<Grade> grades = new HashSet<>();
            for (Long gid : gradeIds) {
                gradeService.findById(gid).ifPresent(grades::add);
            }
            user.setGrades(grades);
        }
        
        // Set subjects for teachers
        if (subjectIds != null) {
            Set<Subject> subjects = new HashSet<>();
            for (Long sid : subjectIds) {
                subjectService.findById(sid).ifPresent(subjects::add);
            }
            user.setSubjects(subjects);
        }
        
        // Create or update
        if (user.getId() == null) {
            userService.createUser(user);
            ra.addFlashAttribute("success", "User created!");
        } else {
            userService.updateUser(user);
            ra.addFlashAttribute("success", "User updated!");
        }
        return "redirect:/admin/users";
    }
    
    // Toggle enable/disable user
    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.toggleUserStatus(id);
        ra.addFlashAttribute("success", "User status updated!");
        return "redirect:/admin/users";
    }
    
    // Delete user
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted!");
        return "redirect:/admin/users";
    }
    
    // ========== GRADE MANAGEMENT ==========
    @GetMapping("/grades")
    public String grades(Model model) {
        model.addAttribute("grades", gradeService.findAll());
        return "admin/grades";
    }
    
    @PostMapping("/grades/save")
    public String saveGrade(@RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            RedirectAttributes ra) {
        Grade grade = new Grade();
        grade.setName(name);
        grade.setDescription(description);
        gradeService.save(grade);
        ra.addFlashAttribute("success", "Grade saved!");
        return "redirect:/admin/grades";
    }
    
    @GetMapping("/grades/delete/{id}")
    public String deleteGrade(@PathVariable("id") Long id, RedirectAttributes ra) {
        gradeService.delete(id);
        ra.addFlashAttribute("success", "Grade deleted!");
        return "redirect:/admin/grades";
    }
    
    // ========== SUBJECT MANAGEMENT ==========
    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "admin/subjects";
    }
    
    // ========== ANNOUNCEMENTS ==========
    @GetMapping("/announcements")
    public String announcements(Model model) {
        model.addAttribute("announcements", announcementService.findAll());
        return "admin/announcements";
    }
    
    @PostMapping("/announcements/save")
    public String saveAnnouncement(@RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("targetAudience") Announcement.TargetAudience target,
            @RequestParam(value = "pinned", defaultValue = "false") boolean pinned,
            Authentication auth,
            RedirectAttributes ra) {
        
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Announcement a = Announcement.builder()
            .title(title)
            .content(content)
            .targetAudience(target)
            .pinned(pinned)
            .author(user)
            .build();
        
        announcementService.save(a);
        ra.addFlashAttribute("success", "Announcement published!");
        return "redirect:/admin/announcements";
    }
    
    // ========== EVENTS ==========
    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("entryTypes", calendarEntryTypeService.findAll());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("teachers", userService.findByRole(Role.TEACHER));
        return "admin/events";
    }
    
    // ========== TIMETABLE ==========
    @GetMapping("/timetable")
    public String timetable(Model model) {
        model.addAttribute("timetableEntries", timetableService.findAll());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("teachers", userService.findByRole(Role.TEACHER));
        return "admin/timetable";
    }
    
    @PostMapping("/timetable/save")
    public String saveTimetable(@RequestParam("gradeId") Long gradeId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam("dayOfWeek") Timetable.DayOfWeek dayOfWeek,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("endTime") LocalTime endTime,
            @RequestParam(value = "room", required = false) String room,
            RedirectAttributes ra) {
        
        Timetable tt = Timetable.builder()
            .grade(gradeService.findById(gradeId).orElseThrow())
            .subject(subjectService.findById(subjectId).orElseThrow())
            .teacher(userService.findById(teacherId).orElseThrow())
            .dayOfWeek(dayOfWeek)
            .startTime(startTime)
            .endTime(endTime)
            .room(room)
            .build();
        
        // Validate no conflicts
        List<String> errors = timetableService.validate(tt);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("error", String.join(" | ", errors));
            return "redirect:/admin/timetable";
        }
        
        timetableService.save(tt);
        ra.addFlashAttribute("success", "Timetable slot added!");
        return "redirect:/admin/timetable";
    }
}
```

### 2. TeacherController.java - Teacher Functions

```java
@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {
    
    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        
        // Get teacher's courses
        List<Course> courses = courseService.findByTeacher(teacher);
        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courses.size());
        
        // Get pinned courses (max 4)
        List<Course> pinned = new ArrayList<>(teacher.getPinnedCourses());
        if (pinned.size() > 4) pinned = pinned.subList(0, 4);
        model.addAttribute("pinnedCourses", pinned);
        
        // Recent announcements
        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        
        // Activity feed
        model.addAttribute("recentActivities", 
            activityLogService.getRecentActivitiesForCourses(courses, 15));
        
        return "teacher/dashboard";
    }
    
    // ========== COURSE MANAGEMENT ==========
    @GetMapping("/courses")
    public String courses(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        
        // Calculate stats for each course
        Map<Long, Map<String, Integer>> courseStats = new HashMap<>();
        for (Course c : courses) {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("studentCount", userService.findStudentsByGrade(c.getGrade()).size());
            stats.put("assignmentCount", assignmentService.findByCourse(c).size());
            stats.put("materialCount", courseService.getMaterials(c).size());
            courseStats.put(c.getId(), stats);
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("courseStats", courseStats);
        return "teacher/courses";
    }
    
    // Create new course
    @PostMapping("/courses/save")
    public String saveCourse(@RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("gradeId") Long gradeId,
            Authentication auth,
            RedirectAttributes ra) {
        
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        
        Course course = Course.builder()
            .title(title)
            .description(description)
            .subject(subjectService.findById(subjectId).orElseThrow())
            .grade(gradeService.findById(gradeId).orElseThrow())
            .teacher(teacher)
            .active(true)
            .build();
        
        courseService.save(course);
        ra.addFlashAttribute("success", "Course created successfully!");
        return "redirect:/teacher/courses";
    }
    
    // View course details with materials, assignments, exams
    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable("id") Long id, Model model) {
        Course course = courseService.findById(id).orElseThrow();
        model.addAttribute("course", course);
        model.addAttribute("materials", courseService.getMaterials(course));
        model.addAttribute("assignments", assignmentService.findByCourse(course));
        model.addAttribute("exams", examService.findByCourse(course));
        return "teacher/course-detail";
    }
    
    // ========== ASSIGNMENT MANAGEMENT ==========
    @GetMapping("/assignments")
    public String assignments(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        
        Map<Long, List<Assignment>> assignmentsByCourse = new HashMap<>();
        Map<Long, Integer> submissionCountByAssignment = new HashMap<>();
        
        for (Course c : courses) {
            List<Assignment> assignments = assignmentService.findByCourse(c);
            assignmentsByCourse.put(c.getId(), assignments);
            
            for (Assignment a : assignments) {
                submissionCountByAssignment.put(a.getId(), 
                    assignmentService.getSubmissions(a).size());
            }
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("assignmentsByCourse", assignmentsByCourse);
        model.addAttribute("submissionCountByAssignment", submissionCountByAssignment);
        return "teacher/assignments";
    }
    
    // Create assignment
    @PostMapping("/assignments/save")
    public String saveAssignment(@RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("dueDate") LocalDateTime dueDate,
            @RequestParam("maxMarks") int maxMarks,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication auth,
            RedirectAttributes ra) {
        
        Course course = courseService.findById(courseId).orElseThrow();
        
        Assignment assignment = Assignment.builder()
            .title(title)
            .description(description)
            .dueDate(dueDate)
            .maxMarks(maxMarks)
            .course(course)
            .build();
        
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            String path = fileStorageService.store(file, "assignments");
            assignment.setAttachmentPath(path);
            assignment.setAttachmentName(file.getOriginalFilename());
        }
        
        assignmentService.save(assignment);
        ra.addFlashAttribute("success", "Assignment created!");
        return "redirect:/teacher/courses/" + courseId;
    }
    
    // View all submissions for an assignment
    @GetMapping("/assignments/{id}/submissions")
    public String viewSubmissions(@PathVariable("id") Long id, Model model) {
        Assignment assignment = assignmentService.findById(id).orElseThrow();
        List<AssignmentSubmission> submissions = assignmentService.getSubmissions(assignment);
        
        // Calculate statistics
        int totalSubmissions = submissions.size();
        int gradedCount = (int) submissions.stream().filter(AssignmentSubmission::isGraded).count();
        
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", submissions);
        model.addAttribute("totalSubmissions", totalSubmissions);
        model.addAttribute("gradedCount", gradedCount);
        return "teacher/submissions";
    }
    
    // Grade a submission
    @PostMapping("/submissions/{id}/grade")
    public String gradeSubmission(@PathVariable("id") Long submissionId,
            @RequestParam("marks") int marks,
            @RequestParam("feedback") String feedback,
            RedirectAttributes ra) {
        
        assignmentService.gradeSubmission(submissionId, marks, feedback);
        ra.addFlashAttribute("success", "Submission graded!");
        return "redirect:/teacher/assignments";
    }
    
    // ========== EXAM/QUIZ MANAGEMENT ==========
    @GetMapping("/exams")
    public String exams(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        
        // Get all exams by course
        Map<Long, List<Exam>> examsByCourse = new HashMap<>();
        Map<Long, Integer> attemptCountByExam = new HashMap<>();
        
        for (Course c : courses) {
            List<Exam> exams = examService.findByCourse(c);
            examsByCourse.put(c.getId(), exams);
            
            for (Exam e : exams) {
                attemptCountByExam.put(e.getId(), 
                    examService.getAttempts(e).size());
            }
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("examsByCourse", examsByCourse);
        model.addAttribute("attemptCountByExam", attemptCountByExam);
        return "teacher/exams";
    }
    
    // Create exam
    @PostMapping("/exams/save")
    public String saveExam(@RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam("type") Exam.ExamType type,
            @RequestParam("durationMinutes") int duration,
            @RequestParam("totalMarks") int marks,
            RedirectAttributes ra) {
        
        Course course = courseService.findById(courseId).orElseThrow();
        
        Exam exam = Exam.builder()
            .title(title)
            .type(type)
            .durationMinutes(duration)
            .totalMarks(marks)
            .course(course)
            .published(false)  // Draft by default
            .build();
        
        examService.save(exam);
        ra.addFlashAttribute("success", "Exam created! Add questions now.");
        return "redirect:/teacher/exams/" + exam.getId() + "/questions";
    }
    
    // Add question to exam
    @PostMapping("/exams/{id}/questions/save")
    public String saveQuestion(@PathVariable("id") Long examId,
            @RequestParam("questionText") String text,
            @RequestParam("questionType") QuestionType type,
            @RequestParam("optionA") String optA,
            @RequestParam("optionB") String optB,
            @RequestParam("optionC") String optC,
            @RequestParam("optionD") String optD,
            @RequestParam("correctAnswer") String correct,
            @RequestParam("marks") int marks,
            RedirectAttributes ra) {
        
        Exam exam = examService.findById(examId).orElseThrow();
        
        ExamQuestion question = ExamQuestion.builder()
            .exam(exam)
            .questionType(type)
            .questionText(text)
            .optionA(optA).optionB(optB).optionC(optC).optionD(optD)
            .correctAnswer(correct)
            .marks(marks)
            .questionOrder(exam.getQuestions().size() + 1)
            .build();
        
        examService.saveQuestion(question);
        ra.addFlashAttribute("success", "Question added!");
        return "redirect:/teacher/exams/" + examId + "/questions";
    }
    
    // Publish/unpublish exam
    @PostMapping("/exams/{id}/toggle-publish")
    public String togglePublish(@PathVariable("id") Long id, RedirectAttributes ra) {
        Exam exam = examService.findById(id).orElseThrow();
        exam.setPublished(!exam.isPublished());
        examService.save(exam);
        ra.addFlashAttribute("success", 
            exam.isPublished() ? "Exam published!" : "Exam unpublished.");
        return "redirect:/teacher/exams";
    }
    
    // ========== ATTENDANCE MANAGEMENT ==========
    @GetMapping("/attendance")
    public String attendance(Model model, Authentication auth) {
        User teacher = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = courseService.findByTeacher(teacher);
        
        model.addAttribute("courses", courses);
        model.addAttribute("today", LocalDate.now());
        return "teacher/attendance";
    }
    
    @GetMapping("/attendance/course/{id}")
    public String attendanceForCourse(@PathVariable("id") Long id,
            @RequestParam(value = "date", required = false) LocalDate date,
            Model model) {
        
        Course course = courseService.findById(id).orElseThrow();
        LocalDate targetDate = date != null ? date : LocalDate.now();
        
        // Get all students in this grade
        List<User> students = userService.findStudentsByGrade(course.getGrade());
        
        // Get existing attendance records for this date
        Map<Long, Attendance> existingAttendance = new HashMap<>();
        for (Attendance a : attendanceService.findByCourseAndDate(course, targetDate)) {
            existingAttendance.put(a.getStudent().getId(), a);
        }
        
        model.addAttribute("course", course);
        model.addAttribute("students", students);
        model.addAttribute("date", targetDate);
        model.addAttribute("existingAttendance", existingAttendance);
        return "teacher/attendance-mark";
    }
    
    // Save attendance
    @PostMapping("/attendance/save")
    public String saveAttendance(@RequestParam("courseId") Long courseId,
            @RequestParam("date") LocalDate date,
            @RequestParam Map<String, String> params,
            Authentication auth,
            RedirectAttributes ra) {
        
        Course course = courseService.findById(courseId).orElseThrow();
        List<User> students = userService.findStudentsByGrade(course.getGrade());
        
        for (User student : students) {
            String statusParam = params.get("status_" + student.getId());
            String remarksParam = params.get("remarks_" + student.getId());
            
            AttendanceStatus status = AttendanceStatus.valueOf(statusParam);
            
            // Find existing or create new
            Attendance attendance = attendanceService
                .findByCourseAndStudentAndDate(course, student, date)
                .orElse(Attendance.builder()
                    .course(course)
                    .student(student)
                    .date(date)
                    .build());
            
            attendance.setStatus(status);
            attendance.setRemarks(remarksParam);
            attendanceService.save(attendance);
        }
        
        ra.addFlashAttribute("success", "Attendance saved!");
        return "redirect:/teacher/attendance/course/" + courseId + "?date=" + date;
    }
}
```

### 3. StudentController.java - Student Functions

```java
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    
    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        
        // Get student's courses (based on their grade)
        List<Course> courses = student.getGrade() != null 
            ? courseService.findByGrade(student.getGrade()) 
            : Collections.emptyList();
        
        model.addAttribute("courses", courses);
        
        // Pinned courses (max 4)
        List<Course> pinned = new ArrayList<>(student.getPinnedCourses());
        if (pinned.size() > 4) pinned = pinned.subList(0, 4);
        model.addAttribute("pinnedCourses", pinned);
        
        // Recent announcements
        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        
        // PENDING ASSIGNMENTS (not submitted, not overdue)
        List<Assignment> pending = new ArrayList<>();
        for (Course c : courses) {
            for (Assignment a : assignmentService.findByCourse(c)) {
                if (!a.isOverdue() && !assignmentService.hasSubmitted(a, student)) {
                    pending.add(a);
                }
            }
        }
        model.addAttribute("pendingAssignments", pending);
        
        // AVAILABLE QUIZZES (published, not attempted)
        List<Exam> quizzes = examService.findPublishedByCourses(courses);
        List<Exam> available = new ArrayList<>();
        for (Exam e : quizzes) {
            if (!examService.hasAttempted(e, student)) {
                available.add(e);
            }
        }
        model.addAttribute("availableQuizzes", available);
        
        return "student/dashboard";
    }
    
    // ========== COURSES ==========
    @GetMapping("/courses")
    public String courses(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null 
            ? courseService.findByGrade(student.getGrade()) 
            : Collections.emptyList();
        
        // Group by subject for display
        Map<String, List<Course>> coursesBySubject = new LinkedHashMap<>();
        for (Course course : courses) {
            String subjectName = course.getSubject().getName();
            coursesBySubject.computeIfAbsent(subjectName, k -> new ArrayList<>()).add(course);
        }
        
        // Per-course stats
        Map<Long, Map<String, Integer>> courseStats = new HashMap<>();
        for (Course c : courses) {
            Map<String, Integer> stats = new HashMap<>();
            
            // Assignment stats
            List<Assignment> assignments = assignmentService.findByCourse(c);
            int pending = 0, completed = 0;
            for (Assignment a : assignments) {
                if (assignmentService.hasSubmitted(a, student)) {
                    completed++;
                } else if (!a.isOverdue()) {
                    pending++;
                }
            }
            stats.put("pendingAssignments", pending);
            stats.put("completedAssignments", completed);
            
            // Quiz stats
            List<Exam> exams = examService.findByCourse(c);
            int pendingQuizzes = 0, completedQuizzes = 0;
            for (Exam e : exams) {
                if (!e.isPublished()) continue;
                if (examService.hasAttempted(e, student)) {
                    completedQuizzes++;
                } else {
                    pendingQuizzes++;
                }
            }
            stats.put("pendingQuizzes", pendingQuizzes);
            stats.put("completedQuizzes", completedQuizzes);
            
            courseStats.put(c.getId(), stats);
        }
        
        model.addAttribute("coursesBySubject", coursesBySubject);
        model.addAttribute("courseStats", courseStats);
        model.addAttribute("courses", courses);
        return "student/courses";
    }
    
    // View course details
    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable("id") Long id, 
            Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(id).orElseThrow();
        
        model.addAttribute("course", course);
        model.addAttribute("materials", courseService.getMaterials(course));
        
        List<Assignment> assignments = assignmentService.findByCourse(course);
        model.addAttribute("assignments", assignments);
        
        List<Exam> exams = examService.findByCourse(course);
        model.addAttribute("exams", exams);
        
        // Check which assignments are submitted
        Map<Long, Boolean> submitted = new HashMap<>();
        for (Assignment a : assignments) {
            submitted.put(a.getId(), assignmentService.hasSubmitted(a, student));
        }
        model.addAttribute("submitted", submitted);
        
        return "student/course-detail";
    }
    
    // ========== ASSIGNMENTS ==========
    @GetMapping("/assignments")
    public String assignments(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null 
            ? courseService.findByGrade(student.getGrade()) 
            : Collections.emptyList();
        
        // Get all assignments from all courses
        List<Assignment> all = assignmentService.findByCourses(courses);
        
        // Map submission status
        Map<Long, Boolean> submitted = new HashMap<>();
        Map<Long, AssignmentSubmission> submissions = new HashMap<>();
        
        for (Assignment a : all) {
            submitted.put(a.getId(), assignmentService.hasSubmitted(a, student));
            assignmentService.getSubmission(a, student)
                .ifPresent(s -> submissions.put(a.getId(), s));
        }
        
        model.addAttribute("assignments", all);
        model.addAttribute("submitted", submitted);
        model.addAttribute("submissions", submissions);
        return "student/assignments";
    }
    
    // Show assignment submission form
    @GetMapping("/assignments/{id}/submit")
    public String submitForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("assignment", assignmentService.findById(id).orElseThrow());
        return "student/submit-assignment";
    }
    
    // Submit assignment
    @PostMapping("/assignments/{id}/submit")
    public String submitAssignment(@PathVariable("id") Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "comments", required = false) String comments,
            Authentication auth, RedirectAttributes ra) {
        
        try {
            User student = userService.findByUsername(auth.getName()).orElseThrow();
            Assignment assignment = assignmentService.findById(id).orElseThrow();
            
            // Check if already submitted (update vs create)
            AssignmentSubmission sub = assignmentService
                .getSubmission(assignment, student)
                .orElse(AssignmentSubmission.builder()
                    .assignment(assignment)
                    .student(student)
                    .build());
            
            // Handle file upload
            if (file != null && !file.isEmpty()) {
                String path = fileStorageService.store(file, "submissions");
                sub.setFilePath(path);
                sub.setFileName(file.getOriginalFilename());
            }
            
            if (comments != null) {
                sub.setComments(comments);
            }
            
            assignmentService.submitAssignment(sub);
            ra.addFlashAttribute("success", "Assignment submitted successfully!");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/student/assignments";
    }
    
    // ========== QUIZZES/EXAMS ==========
    @GetMapping("/quizzes")
    public String quizzes(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null 
            ? courseService.findByGrade(student.getGrade()) 
            : Collections.emptyList();
        
        // Get published exams for these courses
        List<Exam> publishedExams = examService.findPublishedByCourses(courses);
        
        // Separate into available and completed
        List<Exam> available = new ArrayList<>();
        List<ExamAttempt> completed = new ArrayList<>();
        
        for (Exam e : publishedExams) {
            if (examService.hasAttempted(e, student)) {
                examService.getAttempt(e, student).ifPresent(completed::add);
            } else {
                available.add(e);
            }
        }
        
        model.addAttribute("availableExams", available);
        model.addAttribute("completedAttempts", completed);
        return "student/quizzes";
    }
    
    // Start taking an exam
    @GetMapping("/exams/{id}/take")
    public String takeExam(@PathVariable("id") Long id, 
            Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        Exam exam = examService.findById(id).orElseThrow();
        
        // Check if already attempted
        if (examService.hasAttempted(exam, student)) {
            return "redirect:/student/quizzes?error=already_attempted";
        }
        
        // Create attempt
        ExamAttempt attempt = examService.startAttempt(exam, student);
        
        model.addAttribute("exam", exam);
        model.addAttribute("attempt", attempt);
        model.addAttribute("questions", examService.getQuestions(exam));
        return "student/take-exam";
    }
    
    // Submit exam answers
    @PostMapping("/exams/{attemptId}/submit")
    public String submitExam(@PathVariable("attemptId") Long attemptId,
            @RequestParam Map<String, String> answers,
            Authentication auth,
            RedirectAttributes ra) {
        
        // Extract question answers from form params
        Map<Long, String> questionAnswers = new HashMap<>();
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                questionAnswers.put(questionId, entry.getValue());
            }
        }
        
        // Submit and auto-grade
        ExamAttempt attempt = examService.submitAttempt(attemptId, questionAnswers);
        
        ra.addFlashAttribute("success", 
            String.format("Exam completed! Score: %d/%d (%.1f%%)", 
                attempt.getScore(), attempt.getTotalMarks(), attempt.getPercentage()));
        return "redirect:/student/quizzes";
    }
    
    // ========== GRADES ==========
    @GetMapping("/grades")
    public String grades(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        
        // Get all grades data
        List<UnifiedGradeDTO> allGrades = studentGradeService
            .getStudentGrades(student.getId());
        
        // Calculate GPA and stats
        double gpa = studentGradeService.calculateGPA(allGrades);
        Map<String, Object> stats = studentGradeService.getGradeStatistics(allGrades);
        
        model.addAttribute("grades", allGrades);
        model.addAttribute("gpa", gpa);
        model.addAttribute("stats", stats);
        return "student/grades";
    }
    
    // ========== ATTENDANCE ==========
    @GetMapping("/attendance")
    public String attendance(Model model, Authentication auth) {
        User student = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> courses = student.getGrade() != null 
            ? courseService.findByGrade(student.getGrade()) 
            : Collections.emptyList();
        
        // Get attendance for each course
        Map<Long, List<Attendance>> attendanceByCourse = new HashMap<>();
        Map<Long, Map<String, Integer>> attendanceStats = new HashMap<>();
        
        for (Course c : courses) {
            List<Attendance> records = attendanceService.findByCourseAndStudent(c, student);
            attendanceByCourse.put(c.getId(), records);
            
            // Calculate stats
            int present = 0, absent = 0, late = 0;
            for (Attendance a : records) {
                switch (a.getStatus()) {
                    case PRESENT -> present++;
                    case ABSENT -> absent++;
                    case LATE -> late++;
                }
            }
            Map<String, Integer> stats = new HashMap<>();
            stats.put("present", present);
            stats.put("absent", absent);
            stats.put("late", late);
            stats.put("total", records.size());
            attendanceStats.put(c.getId(), stats);
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("attendanceByCourse", attendanceByCourse);
        model.addAttribute("attendanceStats", attendanceStats);
        return "student/attendance";
    }
}
```

---

## 💬 MESSAGING SYSTEM

### Message Entity

```java
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User sender;              // From
    
    @ManyToOne
    private User recipient;           // To
    
    private String subject;           // Message subject
    private String content;           // Message body
    private boolean read = false;     // Read status
    
    private LocalDateTime sentAt;     // Timestamp
    private LocalDateTime readAt;     // Read timestamp
}
```

### MessageService.java

```java
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    
    // Send message
    @Transactional
    public Message sendMessage(User sender, User recipient, String subject, String content) {
        Message message = Message.builder()
            .sender(sender)
            .recipient(recipient)
            .subject(subject)
            .content(content)
            .sentAt(LocalDateTime.now())
            .read(false)
            .build();
        
        Message saved = messageRepository.save(message);
        
        // Create notification for recipient
        notificationService.createNotification(
            recipient,
            "New message from " + sender.getFullName(),
            subject,
            "/messages/view/" + saved.getId()
        );
        
        return saved;
    }
    
    // Get inbox (received messages)
    public List<Message> getInbox(User user) {
        return messageRepository.findByRecipientOrderBySentAtDesc(user);
    }
    
    // Get sent messages
    public List<Message> getSent(User user) {
        return messageRepository.findBySenderOrderBySentAtDesc(user);
    }
    
    // Get unread count
    public long getUnreadCount(User user) {
        return messageRepository.countByRecipientAndReadFalse(user);
    }
    
    // Mark as read
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        if (!message.isRead()) {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
}
```

---

## 📢 NOTIFICATION SYSTEM

### Notification Entity

```java
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;                // Recipient
    
    private String title;             // Short title
    private String message;           // Full message
    private String link;              // Click action URL
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;    // TYPE: ASSIGNMENT, EXAM, MESSAGE, etc.
    
    private boolean read = false;     // Read status
    private LocalDateTime createdAt;  // Timestamp
}
```

### NotificationService.java

```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    
    // Create notification
    @Transactional
    public Notification createNotification(User user, String title, 
            String message, String link) {
        Notification notification = Notification.builder()
            .user(user)
            .title(title)
            .message(message)
            .link(link)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        
        return notificationRepository.save(notification);
    }
    
    // Get user's notifications
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    // Get unread notifications
    public List<Notification> getUnread(User user) {
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }
    
    // Count unread
    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }
    
    // Mark as read
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        n.setRead(true);
        notificationRepository.save(n);
    }
    
    // Mark all as read
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository
            .findByUserAndReadFalseOrderByCreatedAtDesc(user);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
```

---

## 🗄️ REPOSITORY PATTERN

Repositories are interfaces that extend Spring Data JPA. They automatically generate SQL queries.

### UserRepository.java Example

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by username (for login)
    Optional<User> findByUsername(String username);
    
    // Find by primary ID (ST001, TE001, etc.)
    Optional<User> findByPrimaryId(String primaryId);
    
    // Find all users by role
    List<User> findByRole(Role role);
    
    // Count users by role (for dashboard stats)
    long countByRole(Role role);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if primary ID exists
    boolean existsByPrimaryId(String primaryId);
    
    // Find students in a specific grade
    List<User> findByRoleAndGrade(Role role, Grade grade);
    
    // Search users by name (case insensitive)
    List<User> findByFullNameContainingIgnoreCase(String name);
}
```

### CourseRepository.java Example

```java
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    // Find all courses by teacher
    List<Course> findByTeacher(User teacher);
    
    // Find courses by grade (for students)
    List<Course> findByGradeAndActive(Grade grade, boolean active);
    
    // Find by subject
    List<Course> findBySubject(Subject subject);
    
    // Count courses by teacher
    long countByTeacher(User teacher);
    
    // Find courses where title contains keyword
    List<Course> findByTitleContainingIgnoreCase(String keyword);
    
    // Custom query: Find courses by teacher's grade assignment
    @Query("SELECT c FROM Course c WHERE c.grade IN :grades AND c.active = true")
    List<Course> findByGrades(@Param("grades") Set<Grade> grades);
}
```

### AssignmentRepository.java Example

```java
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    // Find assignments by course, ordered by due date
    List<Assignment> findByCourseOrderByDueDateDesc(Course course);
    
    // Find all assignments for multiple courses
    List<Assignment> findByCourseIn(List<Course> courses);
    
    // Find overdue assignments
    List<Assignment> findByDueDateBefore(LocalDateTime now);
    
    // Count assignments by course
    long countByCourse(Course course);
}
```

---

## 🔧 KEY FEATURES SUMMARY

| Feature | Admin | Teacher | Student |
|---------|-------|---------|---------|
| **User Management** | Create/edit/disable users | View students in courses | View own profile |
| **Course Management** | View all courses | Create/manage own courses | View enrolled courses |
| **Assignments** | - | Create, grade submissions | Submit, view grades |
| **Quizzes/Exams** | - | Create, publish, view results | Take, view scores |
| **Attendance** | - | Mark attendance | View own attendance |
| **Grades** | - | Enter grades | View report card |
| **Announcements** | Create for all | View | View |
| **Messaging** | - | Message students | Message teachers |
| **Calendar** | Create events | View | View |
| **Timetable** | Manage schedule | View assigned | View own schedule |

---

## 🔄 REQUEST FLOW EXAMPLE (Submit Assignment)

```
┌─────────────────────────────────────────────────────────────────┐
│  STUDENT clicks "Submit Assignment"                              │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  1. Browser sends POST to /student/assignments/{id}/submit      │
│     (with file and comments)                                     │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  2. StudentController.submitAssignment() receives request      │
│     • Extracts file and comments                                 │
│     • Gets current user from Authentication                      │
│     • Validates assignment exists                                │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  3. FileStorageService.store() saves file to uploads/submissions/│
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  4. AssignmentService.submitAssignment()                       │
│     • Creates AssignmentSubmission entity                       │
│     • Sets file path, student, assignment                         │
│     • Saves to database via repository                            │
│     • Logs activity for teacher notification                     │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  5. ActivityLogService creates activity entry                     │
│     → Teacher sees notification on dashboard                     │
└──────────────────┬──────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  6. Controller redirects to /student/assignments with success   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📦 DEPENDENCIES (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Tools -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- File Storage (AWS S3 SDK) -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.25.11</version>
    </dependency>
</dependencies>
```

---

## 🚀 QUICK REFERENCE - COMMON ANNOTATIONS

| Annotation | Purpose | Location |
|------------|---------|----------|
| `@SpringBootApplication` | Marks main application class | LmsApplication.java |
| `@Controller` | Handles web requests | Controller classes |
| `@Service` | Business logic layer | Service classes |
| `@Repository` | Data access layer | Repository interfaces |
| `@Entity` | Database table mapping | Model classes |
| `@Table(name = "...")` | Custom table name | Model classes |
| `@Id` | Primary key field | Model fields |
| `@GeneratedValue` | Auto-increment ID | Model fields |
| `@ManyToOne` | Many-to-one relationship | Model fields |
| `@OneToMany` | One-to-many relationship | Model fields |
| `@ManyToMany` | Many-to-many relationship | Model fields |
| `@GetMapping("/path")` | Handle GET requests | Controller methods |
| `@PostMapping("/path")` | Handle POST requests | Controller methods |
| `@RequestParam` | Get form/query parameters | Method parameters |
| `@PathVariable` | Get URL path values | Method parameters |
| `@ModelAttribute` | Bind form to object | Method parameters |
| `@Autowired` / `@RequiredArgsConstructor` | Dependency injection | Class level |
| `@Transactional` | Database transaction | Service methods |
| `@PreAuthorize` | Method-level security | Controller methods |

---

**End of Visual Guide** - This document covers all major functions and features of the EduFlow LMS system. Each code section shows exactly how the feature works from database to user interface.
