package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;

    @Transactional
    public void log(User actor, Course course, ActivityLogType type, String description, String link) {
        ActivityLog log = ActivityLog.builder()
                .actor(actor)
                .course(course)
                .type(type)
                .description(description)
                .link(link)
                .build();
        repository.save(log);
    }

    public List<ActivityLog> getRecentActivitiesForCourses(List<Course> courses, int limit) {
        return repository.findByCourseInOrderByTimestampDesc(courses, PageRequest.of(0, limit));
    }
}
