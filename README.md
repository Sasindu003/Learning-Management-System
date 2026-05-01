<div align="center">

![EduFlow Banner](banner.jpeg)

# 🎓 EduFlow Learning Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M2-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![H2 Database](https://img.shields.io/badge/H2%20Database-Persistent-blue?style=for-the-badge&logo=sqlite&logoColor=white)](http://www.h2database.com/)

---

**EduFlow** is a modern, full-featured Learning Management System designed to bridge the gap between students, teachers, and administrators. Built with security, scalability, and ease of use in mind.

[Explore Features](#🚀-key-features) • [Getting Started](#🛠️-getting-started) • [Tech Stack](#💻-technology-stack)

</div>

## 🚀 Key Features

### 👨‍💼 Admin Dashboard
*   **User Management**: Create and manage student, teacher, and admin accounts with role-based access.
*   **Academic Structure**: Manage subjects, grades, and teacher-subject assignments.
*   **Announcements**: Broadcast important updates to the entire institution.
*   **System Overview**: Monitor and manage all academic activities from a central dashboard.

### 👩‍🏫 Teacher Portal
*   **Course Management**: Create and manage subjects, assignments, and course materials.
*   **Student Tracking**: View enrolled students and monitor their academic performance.
*   **Assignment & Grading**: Create assignments, set deadlines, grade submissions, and provide feedback.
*   **Exam Management**: Create exams with multiple question types and auto-grading.
*   **Attendance Tracking**: Mark and manage student attendance with analytics.
*   **Resource Library**: Upload and share learning materials (PDF, documents) with AI-powered analysis.
*   **Calendar Management**: Schedule classes, events, and deadlines.

### 🧑‍🎓 Student Experience
*   **Personalized Dashboard**: View enrolled subjects, upcoming assignments, and grades at a glance.
*   **Assignment Submission**: Submit assignments online and track submission status.
*   **Exam Portal**: Take online exams with timed assessments and instant results.
*   **Progress Tracking**: View grades, attendance records, and academic performance analytics.
*   **Study Notes**: Personal scratchpad for each course with auto-save functionality.
*   **Course Discussions**: Participate in subject-specific discussion forums.

### 💬 Communication & Collaboration
*   **Real-time Messaging**: Direct messaging between teachers and students via WebSocket.
*   **Notifications**: Instant alerts for assignments, grades, announcements, and messages.
*   **Discussion Forums**: Course-specific threads for Q&A and collaboration.
*   **Teacher Portfolio**: Public teacher profiles showcasing bio and courses.

### 📊 Additional Features
*   **Academic Calendar**: Interactive calendar with classes, exams, and events.
*   **Timetable Management**: Structured weekly schedules for students and teachers.
*   **Activity Logging**: Track user actions across the system for audit purposes.
*   **Login History**: Track recent login activity for security audit.
*   **Dark Mode**: Theme toggle for comfortable viewing.
*   **REST APIs**: JSON endpoints for discussions and study notes (AJAX support).

---

## 💻 Technology Stack

<details>
<summary><b>Backend & Infrastructure</b></summary>

- **Spring Boot 4.0.3**: Core framework with auto-configuration
- **Spring Security**: Authentication and role-based access control (RBAC)
- **Spring Data JPA**: Data persistence with Hibernate
- **Spring WebSocket**: Real-time messaging infrastructure
- **Spring AI (2.0.0-M2)**: PDF document analysis capabilities
- **H2 Database**: File-based persistent database for development
- **Lombok**: Boilerplate code reduction
- **Hibernate Validator**: Input validation
</details>

<details>
<summary><b>Frontend</b></summary>

- **Thymeleaf**: Server-side template engine with Spring Security integration
- **TailwindCSS**: Utility-first CSS framework (via CDN)
- **Chart.js**: Data visualization for analytics
- **Font Awesome**: Icon library
</details>

<details>
<summary><b>File Handling</b></summary>

- **Multipart File Upload**: Support for documents up to 10MB
- **PDF Analysis**: AI-powered document content extraction
</details>

---

## 🛠️ Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+ (or use included wrapper)

### Running the Project

#### Option 1: Using Maven Wrapper (Recommended)
```bash
# Windows
.\Start maven.cmd

# Or using mvnw directly
.\mvnw spring-boot:run
```

#### Option 2: Using System Maven
```bash
mvn spring-boot:run
```

#### Option 3: Build and Run JAR
```bash@
mvn clean package
java -jar target/lms-0.0.1-SNAPSHOT.jar
```


### Environment Variables (Required for Production)

Never commit real credentials in `application.properties`. Configure secrets via environment variables instead:

```bash
export DB_URL='jdbc:postgresql://<host>:5432/postgres'
export DB_USERNAME='postgres'
export DB_PASSWORD='<your-db-password>'
export JPA_DIALECT='org.hibernate.dialect.PostgreSQLDialect'

export R2_BUCKET_NAME='lms'
export R2_ENDPOINT='https://<account-id>.r2.cloudflarestorage.com'
export R2_ACCESS_KEY='<your-r2-access-key>'
export R2_SECRET_KEY='<your-r2-secret-key>'
```

For local development, defaults are safe (`H2` + empty secrets). For production, set all secret variables through your host's secret manager (Render/Railway/Vercel/Docker/Kubernetes) and rotate exposed keys immediately.

### Accessing the Application
- **Web Application**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/lmsdb`
  - Username: `sa`
  - Password: (leave empty)

### Default Accounts
| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin    |

*Create additional users through the Admin Dashboard*

---

## 📂 Project Structure

```text
src/main/java/com/lms/lms/
├── component/          # Utility components (PasswordHasher)
├── config/             # Security, WebSocket, and app configuration
├── controller/         # Web controllers
│   ├── AdminController.java              # Admin dashboard & user management
│   ├── AuthController.java               # Authentication
│   ├── CalendarController.java           # Academic calendar & events
│   ├── CourseActionController.java       # Course enrollment actions
│   ├── CourseDiscussionController.java   # Discussion forums
│   ├── FileController.java               # File upload/download
│   ├── GlobalControllerAdvice.java       # Global model attributes
│   ├── MessageController.java            # Real-time messaging
│   ├── NotificationController.java       # User notifications
│   ├── PortfolioController.java          # Teacher portfolios
│   ├── ProfileController.java            # User profiles
│   ├── StudentController.java            # Student portal
│   ├── TeacherController.java            # Teacher portal
│   └── api/                              # REST API endpoints
│       ├── CourseDiscussionRestController.java
│       └── StudyNoteApiController.java
├── dto/                # Data Transfer Objects
├── model/              # 26 Entity classes (User, Course, Assignment, ActivityLog, etc.)
├── repository/         # Spring Data JPA repositories
└── service/            # Business logic layer

src/main/resources/
├── static/             # CSS, JS, images
├── templates/          # Thymeleaf HTML templates
│   ├── admin/          # Admin views
│   ├── student/        # Student views
│   └── teacher/        # Teacher views
└── application.properties  # App configuration

data/                   # H2 database files
uploads/                # Uploaded files storage
```

---

## 🗄️ Database Configuration

The application uses **H2 file-based database** with persistent storage:
- Database file location: `./data/lmsdb`
- DDL mode: `update` (preserves data between restarts)
- All data is retained after application restart

---

## 📝 Changelog

See [Changelog](http://localhost:8080/changelog) for version history and recent updates.

Current version: **v1.1.0**

## 🔮 Future Features

See [FUTURE_FEATURES.md](FUTURE_FEATURES.md) for planned enhancements including:
- Gamification (leaderboards, badges)
- Advanced analytics (grade heatmaps, attendance charts)
- Downloadable PDF report cards
- Interactive study calendars
- Resource bookmarking

---

<div align="center">

Made with ❤️ for modern education.

</div>
