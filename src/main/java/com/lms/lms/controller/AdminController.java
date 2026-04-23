package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.model.User.Role;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import com.lms.lms.model.Timetable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final CourseService courseService;
    private final AnnouncementService announcementService;
    private final EventService eventService;
    private final AcademicTermService termService;
    private final NotificationService notificationService;
    private final TimetableService timetableService;
    private final MessageService messageService;
    private final CalendarEntryTypeService calendarEntryTypeService;
    private final LoginLogService loginLogService;


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", userService.countByRole(Role.STUDENT));
        model.addAttribute("teacherCount", userService.countByRole(Role.TEACHER));
        model.addAttribute("courseCount", courseService.findAll().size());
        model.addAttribute("gradeCount", gradeService.findAll().size());
        model.addAttribute("recentAnnouncements", announcementService.findRecent());
        model.addAttribute("upcomingEvents", eventService.findUpcoming());
        return "admin/dashboard";
    }

    // === User Management ===
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("teachers", userService.findByRole(Role.TEACHER));
        model.addAttribute("students", userService.findByRole(Role.STUDENT));
        model.addAttribute("admins", userService.findByRole(Role.ADMIN));
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("roles", User.Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user,
            @RequestParam(value = "gradeId", required = false) Long gradeId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
            RedirectAttributes ra) {
        if (user.getId() == null && userService.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already exists!");
            return "redirect:/admin/users/new";
        }

        // For students: single grade
        if (gradeId != null) {
            user.setGrade(gradeService.findById(gradeId).orElse(null));
        }

        // For teachers: multiple grades
        if (gradeIds != null && !gradeIds.isEmpty()) {
            Set<Grade> grades = new HashSet<>();
            for (Long gid : gradeIds) {
                gradeService.findById(gid).ifPresent(grades::add);
            }
            user.setGrades(grades);
        } else {
            user.setGrades(new HashSet<>());
        }

        if (subjectIds != null) {
            Set<Subject> subjects = new HashSet<>();
            for (Long sid : subjectIds) {
                subjectService.findById(sid).ifPresent(subjects::add);
            }
            user.setSubjects(subjects);
        }

        if (user.getId() == null) {
            userService.createUser(user);
            ra.addFlashAttribute("success", "User created successfully!");
        } else {
            User existing = userService.findById(user.getId()).orElseThrow();

            // Allow username update with uniqueness check
            if (!existing.getUsername().equalsIgnoreCase(user.getUsername())) {
                if (userService.existsByUsername(user.getUsername())) {
                    ra.addFlashAttribute("error", "Username already exists!");
                    return "redirect:/admin/users/edit/" + user.getId();
                }
                existing.setUsername(user.getUsername());
            }

            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            existing.setPhone(user.getPhone());
            existing.setRole(user.getRole());
            existing.setGrade(user.getGrade());
            existing.setGrades(user.getGrades());
            existing.setSubjects(user.getSubjects());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userService.updatePassword(user.getId(), user.getPassword());
            }
            userService.updateUser(existing);
            ra.addFlashAttribute("success", "User updated successfully!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id).orElseThrow());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("roles", User.Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.toggleUserStatus(id);
        ra.addFlashAttribute("success", "User status updated!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted!");
        return "redirect:/admin/users";
    }

    // === Grade Management ===
    @GetMapping("/grades")
    public String grades(Model model) {
        model.addAttribute("grades", gradeService.findAll());
        return "admin/grades";
    }

    @PostMapping("/grades/save")
    public String saveGrade(@RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "id", required = false) Long id,
            RedirectAttributes ra) {
        Optional<Grade> existing = gradeService.findByName(name);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            ra.addFlashAttribute("error", "Grade name already exists!");
            return "redirect:/admin/grades";
        }

        Grade grade = id != null ? gradeService.findById(id).orElse(new Grade()) : new Grade();
        grade.setName(name);
        grade.setDescription(description);
        gradeService.save(grade);
        ra.addFlashAttribute("success", "Grade saved successfully!");
        return "redirect:/admin/grades";
    }

    @GetMapping("/grades/delete/{id}")
    public String deleteGrade(@PathVariable("id") Long id, RedirectAttributes ra) {
        gradeService.delete(id);
        ra.addFlashAttribute("success", "Grade deleted!");
        return "redirect:/admin/grades";
    }

    // === Subject Management ===
    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "admin/subjects";
    }

    @PostMapping("/subjects/save")
    public String saveSubject(@RequestParam("name") String name,
            @RequestParam("code") String code,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "id", required = false) Long id,
            RedirectAttributes ra) {

        // Check for duplicate name
        Optional<Subject> existingName = subjectService.findByName(name);
        if (existingName.isPresent() && (id == null || !existingName.get().getId().equals(id))) {
            ra.addFlashAttribute("error", "Subject name already exists!");
            return "redirect:/admin/subjects";
        }

        // Check for duplicate code
        Optional<Subject> existingCode = subjectService.findByCode(code);
        if (existingCode.isPresent() && (id == null || !existingCode.get().getId().equals(id))) {
            ra.addFlashAttribute("error", "Subject code already exists!");
            return "redirect:/admin/subjects";
        }

        Subject subject = id != null ? subjectService.findById(id).orElse(new Subject()) : new Subject();
        subject.setName(name);
        subject.setCode(code);
        subject.setDescription(description);
        subjectService.save(subject);
        ra.addFlashAttribute("success", "Subject saved successfully!");
        return "redirect:/admin/subjects";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id, RedirectAttributes ra) {
        subjectService.delete(id);
        ra.addFlashAttribute("success", "Subject deleted!");
        return "redirect:/admin/subjects";
    }

    // === Announcements ===
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
                .title(title).content(content).targetAudience(target).pinned(pinned).author(user)
                .build();
        announcementService.save(a);
        ra.addFlashAttribute("success", "Announcement published!");
        return "redirect:/admin/announcements";
    }

    @GetMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable("id") Long id, RedirectAttributes ra) {
        announcementService.delete(id);
        ra.addFlashAttribute("success", "Announcement deleted!");
        return "redirect:/admin/announcements";
    }

    // === Events ===
    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("entryTypes", calendarEntryTypeService.findAll());
        model.addAttribute("eventTypes", calendarEntryTypeService.findAll());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("teachers", userService.findByRole(Role.TEACHER));
        return "admin/events";
    }

    @PostMapping("/events/save")
    public String saveEvent(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("eventDate") java.time.LocalDate eventDate,
            @RequestParam(value = "allDay", defaultValue = "true") boolean allDay,
            @RequestParam(value = "eventTime", required = false) String eventTime,
            @RequestParam(value = "entryTypeId", required = false) Long entryTypeId,
            @RequestParam(value = "targetGradeIds", required = false) List<Long> targetGradeIds,
            @RequestParam(value = "targetSubjectIds", required = false) List<Long> targetSubjectIds,
            @RequestParam(value = "targetTeacherIds", required = false) List<Long> targetTeacherIds,
            Authentication auth,
            RedirectAttributes ra) {

        Event e;
        if (id != null) {
            e = eventService.findById(id).orElse(new Event());
        } else {
            e = new Event();
            User user = userService.findByUsername(auth.getName()).orElseThrow();
            e.setCreatedBy(user);
        }

        e.setTitle(title);
        e.setDescription(description);
        e.setEventDate(eventDate);
        e.setAllDay(allDay);
        e.setEventTime(allDay ? null : eventTime);

        if (entryTypeId != null) {
            calendarEntryTypeService.findById(entryTypeId).ifPresent(e::setEntryType);
        } else {
            e.setEntryType(null);
        }

        // Targeting
        Set<Grade> grades = new HashSet<>();
        if (targetGradeIds != null) {
            for (Long gid : targetGradeIds) {
                gradeService.findById(gid).ifPresent(grades::add);
            }
        }
        e.setTargetGrades(grades);

        Set<Subject> subjects = new HashSet<>();
        if (targetSubjectIds != null) {
            for (Long sid : targetSubjectIds) {
                subjectService.findById(sid).ifPresent(subjects::add);
            }
        }
        e.setTargetSubjects(subjects);

        Set<User> teachers = new HashSet<>();
        if (targetTeacherIds != null) {
            for (Long tid : targetTeacherIds) {
                userService.findById(tid).ifPresent(teachers::add);
            }
        }
        e.setTargetTeachers(teachers);

        eventService.save(e);
        ra.addFlashAttribute("success", id != null ? "Event updated!" : "Event created!");
        return "redirect:/admin/events";
    }

    @GetMapping("/events/edit/{id}")
    @ResponseBody
    public Map<String, Object> getEventJson(@PathVariable("id") Long id) {
        Event e = eventService.findById(id).orElseThrow();
        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("title", e.getTitle());
        map.put("description", e.getDescription());
        map.put("eventDate", e.getEventDate().toString());
        map.put("allDay", e.getAllDay());
        map.put("eventTime", e.getEventTime());
        map.put("entryTypeId", e.getEntryType() != null ? e.getEntryType().getId() : null);
        map.put("targetGradeIds", e.getTargetGrades().stream().map(Grade::getId).toList());
        map.put("targetSubjectIds", e.getTargetSubjects().stream().map(Subject::getId).toList());
        map.put("targetTeacherIds", e.getTargetTeachers().stream().map(User::getId).toList());
        return map;
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id, RedirectAttributes ra) {
        eventService.delete(id);
        ra.addFlashAttribute("success", "Event deleted!");
        return "redirect:/admin/events";
    }

    @PostMapping("/event-types/save")
    public String saveEventType(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "color", defaultValue = "#6366f1") String color,
            @RequestParam(value = "icon", defaultValue = "📅") String icon,
            RedirectAttributes ra) {
        CalendarEntryType type = id != null
                ? calendarEntryTypeService.findById(id).orElse(new CalendarEntryType())
                : new CalendarEntryType();
        type.setName(name);
        type.setColor(color);
        type.setIcon(icon);
        calendarEntryTypeService.save(type);
        ra.addFlashAttribute("success", "Event type saved!");
        return "redirect:/admin/events";
    }

    @GetMapping("/event-types/delete/{id}")
    public String deleteEventType(@PathVariable("id") Long id, RedirectAttributes ra) {
        calendarEntryTypeService.delete(id);
        ra.addFlashAttribute("success", "Event type deleted!");
        return "redirect:/admin/events";
    }

    // === Academic Terms ===
    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("terms", termService.findAll());
        return "admin/terms";
    }

    @PostMapping("/terms/save")
    public String saveTerm(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("startDate") java.time.LocalDate startDate,
            @RequestParam("endDate") java.time.LocalDate endDate,
            @RequestParam(value = "active", defaultValue = "false") boolean active,
            RedirectAttributes ra) {
        AcademicTerm term = id != null ? termService.findById(id).orElse(new AcademicTerm()) : new AcademicTerm();
        term.setName(name);
        term.setStartDate(startDate);
        term.setEndDate(endDate);
        term.setActive(active);

        List<String> errors = termService.validate(term);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("error", String.join(" | ", errors));
            return "redirect:/admin/terms";
        }

        termService.save(term);
        ra.addFlashAttribute("success", id != null ? "Term updated!" : "Term saved!");
        return "redirect:/admin/terms";
    }

    @GetMapping("/terms/edit/{id}")
    @ResponseBody
    public Map<String, Object> getTermJson(@PathVariable("id") Long id) {
        AcademicTerm t = termService.findById(id).orElseThrow();
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId());
        map.put("name", t.getName());
        map.put("startDate", t.getStartDate().toString());
        map.put("endDate", t.getEndDate().toString());
        map.put("active", t.isActive());
        return map;
    }

    @PostMapping("/terms/toggle/{id}")
    public String toggleTerm(@PathVariable("id") Long id, RedirectAttributes ra) {
        termService.toggleStatus(id);
        ra.addFlashAttribute("success", "Term status updated!");
        return "redirect:/admin/terms";
    }

    @GetMapping("/terms/delete/{id}")
    public String deleteTerm(@PathVariable("id") Long id, RedirectAttributes ra) {
        termService.delete(id);
        ra.addFlashAttribute("success", "Term deleted!");
        return "redirect:/admin/terms";
    }

    // === Courses Overview ===
    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "admin/courses";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            courseService.deleteCourse(id);
            ra.addFlashAttribute("success", "Course and all associated content deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // === Timetable Management ===
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
            @RequestParam("startTime") java.time.LocalTime startTime,
            @RequestParam("endTime") java.time.LocalTime endTime,
            @RequestParam(value = "room", required = false) String room,
            RedirectAttributes ra) {
        Timetable tt = Timetable.builder()
                .grade(gradeService.findById(gradeId).orElseThrow())
                .subject(subjectService.findById(subjectId).orElseThrow())
                .teacher(userService.findById(teacherId).orElseThrow())
                .dayOfWeek(dayOfWeek).startTime(startTime).endTime(endTime).room(room)
                .build();

        List<String> errors = timetableService.validate(tt);
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("error", String.join(" | ", errors));
            return "redirect:/admin/timetable";
        }

        timetableService.save(tt);
        ra.addFlashAttribute("success", "Timetable slot added!");
        return "redirect:/admin/timetable";
    }

    @GetMapping("/timetable/delete/{id}")
    public String deleteTimetable(@PathVariable("id") Long id, RedirectAttributes ra) {
        timetableService.delete(id);
        ra.addFlashAttribute("success", "Timetable slot deleted!");
        return "redirect:/admin/timetable";
    }

    // === Announcement Edit ===
    @GetMapping("/announcements/edit/{id}")
    @ResponseBody
    public Map<String, Object> getAnnouncementJson(@PathVariable("id") Long id) {
        Announcement a = announcementService.findById(id).orElseThrow();
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("content", a.getContent());
        map.put("targetAudience", a.getTargetAudience().name());
        map.put("pinned", a.isPinned());
        return map;
    }

    @PostMapping("/announcements/edit/{id}")
    public String updateAnnouncement(@PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("targetAudience") Announcement.TargetAudience target,
            @RequestParam(value = "pinned", defaultValue = "false") boolean pinned,
            RedirectAttributes ra) {
        Announcement a = announcementService.findById(id).orElseThrow();
        a.setTitle(title);
        a.setContent(content);
        a.setTargetAudience(target);
        a.setPinned(pinned);
        announcementService.save(a);
        ra.addFlashAttribute("success", "Announcement updated!");
        return "redirect:/admin/announcements";
    }

    // === Dev Quick Login ===
    @PostMapping("/users/login-as/{id}")
    public String loginAs(@PathVariable("id") Long id, RedirectAttributes ra) {
        User target = userService.findById(id).orElse(null);
        if (target == null) {
            ra.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/users";
        }
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + target.getRole().name()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                target.getUsername(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/dashboard";
    }

    @GetMapping("/teachers/{id}/assignments")
    @ResponseBody
    public Map<String, Object> getTeacherAssignments(@PathVariable("id") Long id) {
        User teacher = userService.findById(id).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        
        List<Map<String, Object>> gradesList = teacher.getGrades().stream().map(g -> {
            Map<String, Object> gm = new HashMap<>();
            gm.put("id", g.getId());
            gm.put("name", g.getName());
            return gm;
        }).toList();
        
        List<Map<String, Object>> subjectsList = teacher.getSubjects().stream().map(s -> {
            Map<String, Object> sm = new HashMap<>();
            sm.put("id", s.getId());
            sm.put("name", s.getName());
            return sm;
        }).toList();

        response.put("grades", gradesList);
        response.put("subjects", subjectsList);
        return response;
    }

    // === Login History ===
    @GetMapping("/login-history")
    public String loginHistory(Model model) {
        model.addAttribute("logs", loginLogService.getAllLogs());
        return "admin/login-history";
    }

    @PostMapping("/login-history/delete/{id}")
    public String deleteLog(@PathVariable("id") Long id, RedirectAttributes ra) {
        loginLogService.deleteLog(id);
        ra.addFlashAttribute("success", "Login log deleted successfully.");
        return "redirect:/admin/login-history";
    }

    @PostMapping("/login-history/clear")
    public String clearAllLogs(RedirectAttributes ra) {
        loginLogService.clearAllLogs();
        ra.addFlashAttribute("success", "All login logs cleared.");
        return "redirect:/admin/login-history";
    }
}
