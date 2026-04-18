package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final UserService userService;
    private final CalendarEntryService calendarEntryService;
    private final CalendarEntryTypeService calendarEntryTypeService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication auth) {
        if (auth == null) return;
        User user = userService.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("currentUser", user);
        if (user != null) {
            model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user));
            model.addAttribute("unreadMessages", messageService.getUnreadCount(user));
        }
    }

    // ===== Page =====
    @GetMapping("/calendar")
    public String calendarPage(Model model, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("entryTypes", calendarEntryTypeService.findVisibleTypes(user));

        // For teachers — pass their assigned grades & subjects for sharing UI
        if (user.getRole() == User.Role.TEACHER) {
            model.addAttribute("teacherGrades", user.getGrades());
            model.addAttribute("teacherSubjects", user.getSubjects());
        }
        return "calendar";
    }

    // ===== REST: Fetch events for FullCalendar =====
    @GetMapping("/api/calendar/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Authentication auth) {

        User user = userService.findByUsername(auth.getName()).orElseThrow();
        List<Map<String, Object>> events = new ArrayList<>();

        // 1. Personal + shared entries
        for (CalendarEntry entry : calendarEntryService.findVisibleEntries(user, start, end)) {
            boolean isOwn = entry.getOwner().getId().equals(user.getId());
            String source = isOwn ? "PERSONAL" : "SHARED";
            String color = entry.getEntryType() != null
                    ? entry.getEntryType().getColor()
                    : (isOwn ? "#6366f1" : "#10b981");

            Map<String, Object> ev = new LinkedHashMap<>();
            ev.put("id", "entry-" + entry.getId());
            ev.put("title", entry.getTitle());
            ev.put("start", entry.getStartDate().toString());
            ev.put("end", entry.getEndDate() != null ? entry.getEndDate().toString() : entry.getStartDate().toString());
            ev.put("allDay", entry.getAllDay() != null && entry.getAllDay());
            if ((entry.getAllDay() == null || !entry.getAllDay()) && entry.getEventTime() != null) {
                ev.put("start", entry.getStartDate() + "T" + entry.getEventTime());
            }
            ev.put("backgroundColor", color);
            ev.put("borderColor", color);

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("source", source);
            props.put("description", entry.getDescription());
            props.put("typeName", entry.getEntryType() != null ? entry.getEntryType().getName() : null);
            props.put("typeIcon", entry.getEntryType() != null ? entry.getEntryType().getIcon() : "📅");
            props.put("eventTime", (entry.getAllDay() != null && entry.getAllDay()) ? null : entry.getEventTime());
            props.put("readOnly", !isOwn);
            props.put("entryId", entry.getId());
            props.put("sharedBy", isOwn ? null : entry.getOwner().getFullName());
            ev.put("extendedProps", props);
            events.add(ev);
        }

        // 2. Admin broadcast events
        for (Event ae : calendarEntryService.findVisibleAdminEvents(user, start, end)) {
            String color = ae.getEntryType() != null ? ae.getEntryType().getColor() : "#ef4444";

            Map<String, Object> ev = new LinkedHashMap<>();
            ev.put("id", "admin-" + ae.getId());
            ev.put("title", ae.getTitle());
            ev.put("start", ae.getEventDate().toString());
            ev.put("end", ae.getEventDate().toString());
            ev.put("allDay", ae.getAllDay() != null && ae.getAllDay());
            if ((ae.getAllDay() == null || !ae.getAllDay()) && ae.getEventTime() != null) {
                ev.put("start", ae.getEventDate() + "T" + ae.getEventTime());
            }
            ev.put("backgroundColor", color);
            ev.put("borderColor", color);

            Map<String, Object> props = new LinkedHashMap<>();
            props.put("source", "ADMIN");
            props.put("description", ae.getDescription());
            props.put("typeName", ae.getEntryType() != null ? ae.getEntryType().getName() : "Event");
            props.put("typeIcon", ae.getEntryType() != null ? ae.getEntryType().getIcon() : "📢");
            props.put("eventTime", (ae.getAllDay() != null && ae.getAllDay()) ? null : ae.getEventTime());
            props.put("readOnly", true);
            props.put("entryId", ae.getId());
            ev.put("extendedProps", props);
            events.add(ev);
        }

        return events;
    }

    // ===== REST: Create personal entry =====
    @PostMapping("/api/calendar/events")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createEntry(
            @RequestBody Map<String, Object> body, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName()).orElseThrow();

            CalendarEntry entry = CalendarEntry.builder()
                    .title((String) body.get("title"))
                    .description((String) body.get("description"))
                    .startDate(LocalDate.parse((String) body.get("startDate")))
                    .endDate(body.get("endDate") != null && !((String) body.get("endDate")).isEmpty()
                            ? LocalDate.parse((String) body.get("endDate")) : null)
                    .allDay(Boolean.parseBoolean(body.getOrDefault("allDay", "true").toString()))
                    .eventTime((String) body.get("eventTime"))
                    .owner(user)
                    .build();

            // Type
            if (body.get("typeId") != null) {
                calendarEntryTypeService.findById(Long.parseLong(body.get("typeId").toString()))
                        .ifPresent(entry::setEntryType);
            }

            // Sharing (teachers only)
            if (user.getRole() == User.Role.TEACHER) {
                boolean shared = Boolean.parseBoolean(body.getOrDefault("shared", "false").toString());
                entry.setShared(shared);
                if (shared) {
                    Set<Grade> grades = new HashSet<>();
                    if (body.get("gradeIds") instanceof List<?> gids) {
                        for (Object gid : gids) {
                            gradeService.findById(Long.parseLong(gid.toString())).ifPresent(grades::add);
                        }
                    }
                    entry.setSharedGrades(grades);

                    Set<Subject> subjects = new HashSet<>();
                    if (body.get("subjectIds") instanceof List<?> sids) {
                        for (Object sid : sids) {
                            subjectService.findById(Long.parseLong(sid.toString())).ifPresent(subjects::add);
                        }
                    }
                    entry.setSharedSubjects(subjects);
                }
            }

            CalendarEntry saved = calendarEntryService.save(entry);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("id", saved.getId());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ===== REST: Delete own entry =====
    @PostMapping("/api/calendar/events/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteEntry(
            @PathVariable("id") Long id, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        CalendarEntry entry = calendarEntryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "Not your entry"));
        }

        calendarEntryService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ===== REST: Entry Types Management =====
    @PostMapping("/api/calendar/entry-types")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveEntryType(
            @RequestBody Map<String, Object> body, Authentication auth) {
        try {
            User user = userService.findByUsername(auth.getName()).orElseThrow();
            
            Long id = body.get("id") != null ? Long.parseLong(body.get("id").toString()) : null;
            CalendarEntryType type;
            
            if (id != null) {
                type = calendarEntryTypeService.findById(id).orElseThrow();
                if (type.getOwner() == null || !type.getOwner().getId().equals(user.getId())) {
                    return ResponseEntity.status(403).body(Map.of("success", false, "error", "Not your entry type"));
                }
            } else {
                type = new CalendarEntryType();
                type.setOwner(user);
            }
            
            type.setName((String) body.get("name"));
            type.setColor((String) body.get("color"));
            type.setIcon((String) body.get("icon"));
            
            CalendarEntryType saved = calendarEntryTypeService.save(type);
            return ResponseEntity.ok(Map.of("success", true, "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/calendar/entry-types/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteEntryType(
            @PathVariable("id") Long id, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        CalendarEntryType type = calendarEntryTypeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found"));

        if (type.getOwner() == null || !type.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "Cannot delete global or other users' types"));
        }

        calendarEntryTypeService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
