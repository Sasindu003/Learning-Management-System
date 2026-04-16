package com.lms.lms.service;

import com.lms.lms.model.Course;
import com.lms.lms.model.CourseDiscussion;
import com.lms.lms.model.User;
import com.lms.lms.repository.CourseDiscussionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseDiscussionService {

    private final CourseDiscussionRepository repository;
    private final CourseService courseService;

    public List<CourseDiscussion> getDiscussionsByCourse(Long courseId) {
        return repository.findByCourseIdOrderByCreatedAtAsc(courseId);
    }

    @Transactional
    public CourseDiscussion save(CourseDiscussion discussion) {
        return repository.save(discussion);
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
        if (userCourses.isEmpty()) return List.of();
        
        return repository.findByCourseInOrderByCreatedAtDesc(
            userCourses, 
            org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }

    public boolean isUserAllowedInCourse(User user, Course course) {
        if (user.getRole() == User.Role.ADMIN) return true;
        if (user.getRole() == User.Role.STUDENT) {
            return user.getGrade() != null && user.getGrade().getId().equals(course.getGrade().getId());
        }
        if (user.getRole() == User.Role.TEACHER) {
            return course.getTeacher().getId().equals(user.getId());
        }
        return false;
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
