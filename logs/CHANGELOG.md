# 📘 EduFlow LMS — Project Changelog & Development Log

> **Project**: EduFlow Learning Management System  
> **Tech Stack**: Spring Boot 4.0.3 + Thymeleaf + H2 Database + Spring Security  
> **Location**: `c:\Users\Killer\Documents\LMS\lms`  
> **Last Updated**: March 17, 2026 (Session 7)

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture Summary](#architecture-summary)
3. [Default Credentials](#default-credentials)
4. [How to Run](#how-to-run)
5. [Change Log (Chronological)](#change-log)
6. [Full File Inventory](#full-file-inventory)
7. [Known Issues & Notes](#known-issues--notes)

---

## Project Overview

A full-stack Learning Management System for an educational institute (~100 students) with a **glassmorphism UI**. Supports three roles: **Admin**, **Teacher**, and **Student**.

### Features

| Epic | Features |
|------|----------|
| **Identity & Access** | Role-based auth, auto-generated IDs (ST001, TE001), login/logout, quick login (admin impersonation), **admin courses overview with search** |
| **Academic Structure** | Grade management (1-12), subject management, course creation, academic terms, **timetable management (admin CRUD, teacher/student views)** |
| **Learning Delivery** | Course materials (file upload), assignments with due dates, quiz/exam engine with MCQs, **delete assignments/exams/materials** |
| **Evaluation** | Auto-graded quizzes, manual assignment grading, grade reports with GPA, attendance tracking, **student attendance summary, teacher attendance history, grade term filter** |
| **Communication** | Internal messaging, announcements with targeting, event calendar, notification system, **announcements view (student/teacher), message reply, announcement edit** |

---

## Architecture Summary

```
19 JPA Entities → 18 Repositories → 16 Services → 7 Controllers → 42 Templates
```

- **Backend**: Spring Boot 4.0.3, Spring Security, Spring Data JPA, H2, Lombok
- **Frontend**: Thymeleaf + Glassmorphism CSS + Vanilla JS
- **Database**: H2 in-memory (auto-seeded on startup)
- **File Storage**: `./uploads/` directory for assignments/materials
- **Passwords**: BCrypt encoding via Spring Security

---

## Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Teacher | `teacher1` | `teacher123` |
| Student | `student1` | `student123` |

---

## How to Run

```bash
cd c:\Users\Killer\Documents\LMS\lms
.\mvnw.cmd clean spring-boot:run
# Open http://localhost:8080
```

To compile only (no run):
```bash
.\mvnw.cmd compile
```

---

## Change Log

### Session 1 — Initial LMS Build (March 4, 2026)

**Goal**: Build the complete LMS from scratch with all 5 epics.

#### What Was Built

**Configuration & Infrastructure:**
- `application.properties` — H2 console enabled, datasource config, file upload limits (50MB), Thymeleaf dev settings
- `SecurityConfig.java` — Role-based URL authorization (`/admin/**`, `/teacher/**`, `/student/**`), custom login page, logout, remember-me
- `DataInitializer.java` — Seeds default admin, sample grades (10), subjects (8), 1 teacher, 1 student
- **pom.xml** — Spring Boot 4.0.3, Spring Security, JPA, H2, Thymeleaf, Lombok

**19 Domain Entities (`model/`):**

| Entity | Purpose |
|--------|---------|
| `User` | Core user: username, password, fullName, email, role, primaryId, enabled |
| `Grade` | Academic class/grade: name, description |
| `Subject` | Subject: name, code, description |
| `Course` | Teacher's course: title, description, subject, grade, teacher |
| `CourseMaterial` | Uploaded files/resources linked to a course |
| `Assignment` | Teacher-created assignment: title, description, dueDate, course, maxMarks |
| `AssignmentSubmission` | Student submission: file, submittedAt, marks, feedback |
| `Exam` | Exam/quiz metadata: title, type, date, duration, course, totalMarks |
| `ExamQuestion` | Question in exam: text, options (A-D), correctAnswer, marks |
| `ExamAttempt` | Student's attempt: score, startTime, endTime |
| `ExamAnswer` | Individual answer per question |
| `StudentGrade` | Marks/grades: student, subject, exam, marks, grade, term |
| `Announcement` | Broadcast: title, content, author, targetRole, createdAt |
| `Message` | Internal message: sender, receiver, subject, content, read |
| `Notification` | Push-style notification: user, message, link, read |
| `Attendance` | Daily attendance: student, date, status, course |
| `Event` | Calendar event: title, date, description, type |
| `Timetable` | Weekly schedule: grade, subject, teacher, dayOfWeek, startTime, endTime |
| `AcademicTerm` | Term/semester: name, startDate, endDate, active |

**18 Repositories — One per entity with custom query methods.**

**16 Services:**

| Service | Key Methods |
|---------|-------------|
| `UserService` | CRUD, authenticate, generatePrimaryId, findByRole |
| `CustomUserDetailsService` | Spring Security UserDetailsService impl |
| `GradeService` | CRUD for grades |
| `SubjectService` | CRUD for subjects |
| `CourseService` | CRUD, findByTeacher, findByGrade |
| `AssignmentService` | Create, submit, grade, findPending |
| `ExamService` | Create exam/quiz, add questions, start/submit attempt, auto-grade |
| `StudentGradeService` | Enter marks, generate report card, analytics |
| `AnnouncementService` | Create, findByTarget, recent |
| `MessageService` | Send, inbox, outbox, markRead |
| `NotificationService` | Create, findUnread, markRead |
| `AttendanceService` | Mark, findByDateAndCourse, stats |
| `EventService` | CRUD events |
| `TimetableService` | CRUD schedules |
| `FileStorageService` | Save/load uploaded files |
| `DashboardService` | Dashboard stats aggregation |

**7 Controllers:**

| Controller | Endpoints |
|------------|-----------|
| `AuthController` | GET/POST `/login`, `/logout` |
| `AdminController` | `/admin/**` — user mgmt, grades, subjects, terms, events, announcements |
| `TeacherController` | `/teacher/**` — courses, materials, assignments, exams, grading, attendance |
| `StudentController` | `/student/**` — courses, submissions, quizzes, grades |
| `MessageController` | `/messages/**` — inbox, compose, view |
| `NotificationController` | `/notifications/**` + REST API for count |
| `ProfileController` | `/profile/**` — view/edit profile, change password |

**35 Thymeleaf Templates:**
- **Layout**: `fragments/layout.html` (master layout with sidebar nav, topbar, notification bell)
- **Login**: `login.html` (glassmorphism dark theme)
- **Admin**: `dashboard.html`, `users.html`, `user-form.html`, `grades.html`, `subjects.html`, `terms.html`, `announcements.html`, `events.html`, `courses.html`, `timetable.html`
- **Teacher**: `dashboard.html`, `courses.html`, `course-detail.html`, `assignment-form.html`, `submissions.html`, `exam-form.html`, `exam-questions.html`, `exam-results.html`, `grading.html`, `attendance.html`, `my-students.html`, `timetable.html`, `announcements.html`, `attendance-history.html`
- **Student**: `dashboard.html`, `courses.html`, `course-detail.html`, `assignments.html`, `submit-assignment.html`, `quizzes.html`, `take-quiz.html`, `grades.html`, `timetable.html`, `attendance.html`, `announcements.html`
- **Shared**: `messages/inbox.html`, `messages/compose.html`, `messages/view.html`, `notifications.html`, `profile/view.html`, `profile/edit.html`

**Static Assets:**
- `static/css/style.css` — Full glassmorphism design system (frosted glass cards, glowing borders, gradient sidebar, animated buttons, responsive grid)
- `static/js/app.js` — Notification polling, quiz timer, form validations, dynamic UI interactions

#### Verification Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | ✅ BUILD SUCCESS |
| Application startup | ✅ Started successfully |
| DataInitializer seeding | ✅ admin, teacher1, student1 created |
| Login page renders | ✅ Glassmorphism dark theme |
| Admin login | ✅ Redirects to dashboard |
| Dashboard stats | ✅ Shows entity counts |
| User management | ✅ Lists teachers and students |
| Grade management | ✅ All 10 grades with student counts |
| Subject management | ✅ All 8 subjects listed |
| Academic terms | ✅ Functional page |

---

### Session 2 — User Profile Management (Feb 25, 2026)

**Goal**: Add grade/subject assignment during user creation, auto-generated primary IDs, separate student/teacher tables, and edit functionality.

#### Changes Made

**Entity: `User.java`** — Added 5 new fields:

| Field | Type | Purpose |
|-------|------|---------|
| `primaryId` | String | Auto-generated ST001/TE001 |
| `grade` | ManyToOne → Grade | Student's enrolled grade |
| `subjects` | ManyToMany → Subject | Student's enrolled subjects |
| `teacherGrades` | ManyToMany → Grade | Teacher's assigned grades |
| `teacherSubject` | ManyToOne → Subject | Teacher's assigned subject |

**Repository: `UserRepository.java`** — Added:
- `findByRole(Role role)` — fetch students/teachers separately
- `countByRole(Role role)` — count users by role for primaryId generation
- `findTopByRoleOrderByIdDesc(Role role)` — get last created user

**Service: `UserService.java`** — Added:
- `generatePrimaryId(Role)` → produces `ST001`, `TE001`, etc.
- `createStudent(...)` → assigns one grade + many subjects
- `createTeacher(...)` → assigns many grades + one subject
- `updateUser(...)` → edits username, fullName, optionally password
- `getStudents()` / `getTeachers()` → role-filtered queries

**Controller: `AdminController.java`** — Added:
- `/admin/users` GET → passes `students`, `teachers`, `grades`, `subjects`, `hasGrades`, `hasSubjects`
- `/admin/users` POST → validates role, creates student or teacher with assignments
- `/admin/users/edit/{id}` GET → returns user JSON for edit modal
- `/admin/users/edit/{id}` POST → saves edited username/fullName/password
- Error validation: blocks user creation if no grades/subjects exist

**Template: `admin/users.html`** — Complete rewrite:
- **Dynamic form**: Role selector (Student/Teacher only), role-specific fields
  - Student → radio buttons for grade (single), checkboxes for subjects (multi)
  - Teacher → checkboxes for grades (multi), radio button for subject (single)
- **Split tables**: Separate Students and Teachers tables (no admins shown)
- **Primary IDs**: Auto-generated ST001/TE001 displayed in tables
- **Edit modal**: Popup form to edit username/fullName/password

**CSS: `style.css`** — Added styles for:
- `.selection-group`, `.selection-item` (checkbox/radio layouts)
- `.primary-id-badge` (styled ID badges)
- `.subject-tag` (subject pills)
- `.modal-overlay`, `.modal-dialog` (edit modal)
- `.alert-link` (error banners)

#### Bug Fix in This Session
- **DataSeeder.java**: Added logic to pre-seed the database with `Mathematics`, `English`, `Sinhala`, and `History` across `Grade 10` and `Grade 11` — ensures forms correctly populate subject/grade options.

---

### Session 3 — Fixing Controller Parameters / Spring Boot 4.x Compatibility (Feb 25, 2026)

**Goal**: Fix `Internal Server Error (500)` during subject creation caused by Spring Boot 4.x parameter name resolution.

#### Problem
Spring Boot 4.x requires explicit `@RequestParam` and `@PathVariable` value names. Without them, parameter binding fails silently at runtime.

#### Fix Applied
Added explicit `value` names to ALL `@RequestParam` and `@PathVariable` annotations across **all controllers**:
- `AdminController.java`
- `TeacherController.java`
- `StudentController.java`

Also added to `pom.xml`:
```xml
<parameters>true</parameters>
```
in the `maven-compiler-plugin` configuration.

#### Example
```java
// Before (broken in Spring Boot 4.x)
@PostMapping("/subjects")
public String createSubject(@RequestParam String name, @RequestParam Long gradeId) { ... }

// After (fixed)
@PostMapping("/subjects")
public String createSubject(@RequestParam("name") String name, @RequestParam("gradeId") Long gradeId) { ... }
```

#### Files Modified
- `AdminController.java` — all `@RequestParam` and `@PathVariable` annotations
- `TeacherController.java` — all `@RequestParam` and `@PathVariable` annotations
- `StudentController.java` — all `@RequestParam` and `@PathVariable` annotations
- `pom.xml` — added `<parameters>true</parameters>`

---

### Session 4 — Refactor Teacher Dashboard + Admin Edit Subjects/Grades (March 3-4, 2026)

**Goal**: (1) Add edit functionality for subjects and grades, (2) Move "My Students" to a separate page from teacher dashboard.

#### Feature 1: Edit Grades (Admin)

- `AdminController.java`:
  - `GET /admin/grades/edit/{id}` — returns grade JSON for edit modal
  - `POST /admin/grades/edit/{id}` — saves grade name update, redirects back
- `admin/grades.html`:
  - Added "Edit" button next to the existing "Delete" button
  - Added hidden edit modal with form for grade name
  - Added JavaScript to populate modal on click and submit via form POST

#### Feature 2: Edit Subjects (Admin)

- `AdminController.java`:
  - `GET /admin/subjects/edit/{id}` — returns subject JSON for edit modal
  - `POST /admin/subjects/edit/{id}` — saves subject name + grade update, redirects back
- `admin/subjects.html`:
  - Added "Edit" button next to the existing "Delete" button
  - Added hidden edit modal with fields for subject name + grade dropdown
  - Added JavaScript to populate modal on click and submit via form POST

#### Feature 3: Teacher Student List (Moved to Separate Page)

- `UserRepository.java` — Added `findByEnrolledTeachersContaining` query method
- `UserService.java` — Added `getStudentsByTeacher(User teacher)` method
- `TeacherController.java` — New endpoint `/teacher/students` for the My Students page
- `teacher/my-students.html` — New template displaying student list grouped by grade
- `dashboard-teacher.html` — Removed inline student list; added "My Students" button above Quick Actions
- `DashboardController.java` — Cleaned up redundant student grouping logic

#### Bug Fix in This Session
- `UserFileRepository.java` — Fixed compilation error (implement new interface method)

---

### Session 5 — Fixing Logout & Quick Login (March 4-5, 2026)

**Goal**: Fix the logout functionality and add quick login buttons for admin to impersonate users.

#### Fix: Logout Not Working

- **Problem**: The sidebar used a GET link for `/logout`, but Spring Security expects POST.
- **SecurityConfig.java**: Updated to handle logout requests correctly (POST method).
- **`fragments/layout.html`**: Changed sidebar logout from `<a>` link to a `<form>` with POST method.

#### Feature: Quick Login (Admin Impersonation)

- **`AdminController.java`**: Added new endpoint for admin quick login — allows administrators to log in as any user directly from the user management page.
- **`admin/users.html`**: Added "Login As" buttons next to each user in the student and teacher tables.

---

### Session 6 — Log File Organization (March 17, 2026)

**Goal**: Organize project root by moving all `.log` files to a `logs/` folder.

#### Files Moved to `logs/`:
- `boot.log` — Application boot log
- `clean.log` — Maven clean output
- `clean2.log` — Maven clean output (2nd run)
- `compile.log` — Maven compile output
- `hs_err_pid12996.log` — JVM crash log
- `hs_err_pid5540.log` — JVM crash log
- `hs_err_pid7376.log` — JVM crash log
- `replay_pid5540.log` — JVM replay log
- `run.log` — Application run log

---

### Session 7 — New Features Across All 5 Epics (March 17, 2026)

**Goal**: Add 15 new professional features across all 5 epics to make the LMS more complete and usable.

#### Features Before vs. After

| Epic | Before | Added in Session 7 |
|------|--------|---------------------|
| Identity & Access | Role-based auth, auto IDs, login/logout, quick login | Admin courses overview with search/filter |
| Academic Structure | Grade/subject/term CRUD, course creation | Timetable management (admin CRUD), student timetable view, teacher timetable view |
| Learning Delivery | Materials upload, assignments, quizzes | Delete assignments, delete exams, delete course materials |
| Evaluation | Auto-grading, manual grading, GPA, attendance | Student attendance summary, teacher attendance history with date picker, grade filtering by term |
| Communication | Messaging, announcements (admin create/delete), events | Announcements view for students/teachers, message reply, announcement editing (admin) |

#### Epic 1 Changes — Identity & Access

**Admin Courses Overview:**
- `AdminController.java` — New `GET /admin/courses` endpoint listing all courses
- `admin/courses.html` — **[NEW]** Courses table with search/filter JS, active/inactive status badges

#### Epic 2 Changes — Academic Structure

**Timetable Management:**
- `AdminController.java` — New endpoints:
  - `GET /admin/timetable` — list all timetable slots with form
  - `POST /admin/timetable/save` — create timetable slot (grade, subject, teacher, day, time, room)
  - `GET /admin/timetable/delete/{id}` — delete slot
- `admin/timetable.html` — **[NEW]** Full CRUD form + table view of all timetable entries
- `TeacherController.java` — New `GET /teacher/timetable` showing teacher's weekly schedule
- `teacher/timetable.html` — **[NEW]** Weekly teaching schedule view
- `StudentController.java` — New `GET /student/timetable` showing student's grade timetable
- `student/timetable.html` — **[NEW]** Weekly student schedule view

#### Epic 3 Changes — Learning Delivery

**Delete Capabilities:**
- `TeacherController.java` — New endpoints:
  - `GET /teacher/assignments/delete/{id}` — delete assignment (redirects to course)
  - `GET /teacher/exams/delete/{id}` — delete exam (redirects to course)
  - `GET /teacher/materials/delete/{id}` — delete course material
- `teacher/course-detail.html` — Added delete buttons (×) next to each material, assignment, and exam

#### Epic 4 Changes — Evaluation

**Student Attendance View:**
- `StudentController.java` — New `GET /student/attendance` showing attendance summary per course
- `student/attendance.html` — **[NEW]** Per-course attendance stats (Present/Absent/Late/Excused counts)

**Teacher Attendance History:**
- `TeacherController.java` — New `GET /teacher/attendance/{courseId}/history?date=yyyy-MM-dd`
- `teacher/attendance-history.html` — **[NEW]** Date picker to view past attendance records

**Grade Filtering by Term:**
- `StudentController.java` — Modified `GET /student/grades?term=...` to accept optional term param
- `student/grades.html` — Added term filter dropdown, clear filter button, deduplication JS

#### Epic 5 Changes — Communication

**Announcements View (Student & Teacher):**
- `StudentController.java` — New `GET /student/announcements` (queries STUDENTS + ALL)
- `student/announcements.html` — **[NEW]** Announcements list with pinned-first sorting
- `TeacherController.java` — New `GET /teacher/announcements` (queries TEACHERS + ALL)
- `teacher/announcements.html` — **[NEW]** Announcements list for teachers

**Message Reply:**
- `MessageController.java` — New `GET /messages/{id}/reply` pre-filling compose form with reply context
- `messages/compose.html` — Updated to support `replyTo`, `replySubject`, `replyContent` pre-fill
- `messages/view.html` — Added "↩ Reply" button next to "← Back to Inbox"

**Announcement Edit (Admin):**
- `AdminController.java` — New endpoints:
  - `GET /admin/announcements/edit/{id}` — returns JSON via `@ResponseBody`
  - `POST /admin/announcements/edit/{id}` — updates announcement
- `admin/announcements.html` — Added "✏ Edit" button, edit modal with AJAX fetch

#### Cross-Cutting Changes

**Sidebar Navigation Updated (`fragments/layout.html`):**
- Admin: Added 📖 Courses, 🕐 Timetable
- Teacher: Added 🕐 My Schedule, 📢 Announcements
- Student: Added 🕐 Timetable, 📋 Attendance, 📢 Announcements

#### New Files Created

| File | Purpose |
|------|---------|
| `admin/courses.html` | All courses overview with search |
| `admin/timetable.html` | Timetable CRUD management |
| `student/timetable.html` | Student's weekly schedule |
| `teacher/timetable.html` | Teacher's weekly schedule |
| `student/attendance.html` | Student attendance summary |
| `student/announcements.html` | Announcements for students |
| `teacher/announcements.html` | Announcements for teachers |
| `teacher/attendance-history.html` | Past attendance viewer |

#### Verification Results

| Check | Result |
|-------|--------|
| `mvn compile` | ✅ BUILD SUCCESS |
| Template count | 42 (was 35, +7 new) |
| Controller endpoints | 15 new endpoints added |
| Sidebar navigation | Updated for all 3 roles |

---

## Full File Inventory

### Java Source Files (`src/main/java/com/lms/lms/`)

```
├── LmsApplication.java              (Main entry point)
├── config/
│   ├── DataInitializer.java          (Database seeder)
│   └── SecurityConfig.java           (Spring Security config)
├── controller/
│   ├── AdminController.java          (Admin routes)
│   ├── AuthController.java           (Login/logout)
│   ├── MessageController.java        (Messaging)
│   ├── NotificationController.java   (Notifications)
│   ├── ProfileController.java        (User profile)
│   ├── StudentController.java        (Student routes)
│   └── TeacherController.java        (Teacher routes)
├── model/
│   ├── AcademicTerm.java
│   ├── Announcement.java
│   ├── Assignment.java
│   ├── AssignmentSubmission.java
│   ├── Attendance.java
│   ├── Course.java
│   ├── CourseMaterial.java
│   ├── Event.java
│   ├── Exam.java
│   ├── ExamAnswer.java
│   ├── ExamAttempt.java
│   ├── ExamQuestion.java
│   ├── Grade.java
│   ├── Message.java
│   ├── Notification.java
│   ├── StudentGrade.java
│   ├── Subject.java
│   ├── Timetable.java
│   └── User.java
├── repository/
│   ├── (18 repository files, one per entity)
└── service/
    ├── (16 service files)
```

### Templates (`src/main/resources/templates/`)

```
├── login.html
├── fragments/
│   └── layout.html
├── admin/
│   ├── announcements.html
│   ├── dashboard.html
│   ├── events.html
│   ├── grades.html
│   ├── subjects.html
│   ├── terms.html
│   ├── user-form.html
│   └── users.html
├── teacher/
│   ├── assignment-form.html
│   ├── attendance.html
│   ├── course-detail.html
│   ├── courses.html
│   ├── dashboard.html
│   ├── exam-form.html
│   ├── exam-questions.html
│   ├── exam-results.html
│   ├── grading.html
│   ├── my-students.html
│   └── submissions.html
├── student/
│   ├── assignments.html
│   ├── course-detail.html
│   ├── courses.html
│   ├── dashboard.html
│   ├── grades.html
│   ├── quizzes.html
│   ├── submit-assignment.html
│   └── take-quiz.html
├── messages/
│   ├── compose.html
│   ├── inbox.html
│   └── view.html
├── notifications.html
└── profile/
    ├── edit.html
    └── view.html
```

### Static Assets (`src/main/resources/static/`)

```
├── css/
│   └── style.css         (Glassmorphism design system)
└── js/
    └── app.js            (Client-side interactions)
```

---

## Known Issues & Notes

1. **Spring Boot 4.x Parameter Names**: All `@RequestParam` and `@PathVariable` annotations MUST include explicit `value` names. Forgetting this will cause 500 errors. The `<parameters>true</parameters>` flag is set in `pom.xml` as a safety net.

2. **H2 In-Memory Database**: Data resets on every restart. The `DataInitializer` re-seeds default data automatically.

3. **JVM Crash Logs**: The `hs_err_pid*.log` files in `logs/` indicate JVM crashes that occurred during development. These are not application bugs — they are JVM-level issues (likely related to memory or native method calls).

4. **File Uploads**: Assignment submissions and course materials are stored in `./uploads/`. This directory is created automatically.

5. **Teacher-Student Relationship**: 
   - Students: 1 grade, many subjects
   - Teachers: many grades, 1 subject
   - This is enforced in the User entity and the admin creation form.

6. **Logout**: Must use POST method (Spring Security CSRF protection). The sidebar uses a form, not a link.

---

> **Note for Future Development**: When moving to a new PC, clone/copy the project folder, ensure Java 21+ and Maven are installed, and run `.\mvnw.cmd clean spring-boot:run`. The embedded H2 database and auto-seeder mean no external database setup is needed.
