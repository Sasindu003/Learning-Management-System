package com.lms.lms.repository;

import com.lms.lms.model.ActivityLog;
import com.lms.lms.model.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByCourseInOrderByTimestampDesc(List<Course> courses, Pageable pageable);
}
