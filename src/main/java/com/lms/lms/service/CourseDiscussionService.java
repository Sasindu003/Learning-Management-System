package com.lms.lms.service;

import com.lms.lms.dto.CourseActivityDTO;
import com.lms.lms.model.*;
import com.lms.lms.repository.CourseDiscussionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseDiscussionService {

    private final CourseDiscussionRepository repository;
    private final CourseService courseService;
    private final ActivityLogService activityLogService;

    public List<CourseDiscussion> getDiscussionsByCourse(Long courseId) {
        return repository.findByCourseIdOrderByCreatedAtAsc(courseId);
    }

    @Transactional
    public CourseDiscussion save(CourseDiscussion discussion) {
        CourseDiscussion saved = repository.save(discussion);
        // Only log if the sender is a student (to avoid cluttering with teacher posts,
        // or adjust if desired)
        if (saved.getSender().getRole() == User.Role.STUDENT) {
            activityLogService.log(
                    saved.getSender(),
                    saved.getCourse(),
                    ActivityLogType.DISCUSSION_POST,
                    saved.getSender().getFullName() + " posted in discussion: " +
                            (saved.getContent().length() > 30 ? saved.getContent().substring(0, 27) + "..."
                                    : saved.getContent()),
                    "/teacher/courses/" + saved.getCourse().getId());
        }
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Optional<CourseDiscussion> findById(Long id) {
        return repository.findById(id);
    }

    public List<CourseDiscussion> getRecentDiscussionsForUser(User user, int limit) {
        List<Course> userCourses = getCoursesForUser(user);
        if (userCourses.isEmpty())
            return List.of();

        return repository.findByCourseInOrderByCreatedAtDesc(
                userCourses,
                org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }

    public boolean isUserAllowedInCourse(User user, Course course) {
        if (user.getRole() == User.Role.ADMIN)
            return true;
        if (user.getRole() == User.Role.STUDENT) {
            return user.getGrade() != null && user.getGrade().getId().equals(course.getGrade().getId());
        }
        if (user.getRole() == User.Role.TEACHER) {
            return course.getTeacher().getId().equals(user.getId());
        }
        return false;
    }

    public List<CourseActivityDTO> getRecentCourseActivities(User user) {
        List<Course> userCourses = getCoursesForUser(user);
        if (userCourses.isEmpty())
            return List.of();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Get all messages for these courses in the last 30 days, sorted by date (DESC)
        List<CourseDiscussion> recentDiscussions = repository.findByCourseInAndCreatedAtAfterOrderByCreatedAtDesc(
                userCourses,
                thirtyDaysAgo);

        // Group by course and take the first (latest) for each, capped at 3 courses
        return recentDiscussions.stream()
                .collect(Collectors.toMap(
                        CourseDiscussion::getCourse,
                        d -> d,
                        (existing, replacement) -> existing, // keep the first one found (latest)
                        LinkedHashMap::new // preserve order
                ))
                .values().stream()
                .limit(3)
                .map(d -> new CourseActivityDTO(
                        d.getCourse().getId(),
                        d.getCourse().getTitle(),
                        d.getContent().length() > 60 ? d.getContent().substring(0, 57) + "..." : d.getContent(),
                        d.getSender().getFullName(),
                        d.getCreatedAt()))
                .collect(Collectors.toList());
    }

    private List<Course> getCoursesForUser(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            return courseService.findAll();
        } else if (user.getRole() == User.Role.STUDENT && user.getGrade() != null) {
            return courseService.findByGrade(user.getGrade());
        } else if (user.getRole() == User.Role.TEACHER) {
            return courseService.findByTeacher(user);
        }
        return List.of();
    }
}
