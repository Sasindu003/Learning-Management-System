package com.lms.lms.repository;

import com.lms.lms.model.CourseDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseDiscussionRepository extends JpaRepository<CourseDiscussion, Long> {
    List<CourseDiscussion> findByCourseIdOrderByCreatedAtAsc(Long courseId);
    
    org.springframework.data.domain.Page<CourseDiscussion> findByCourseInOrderByCreatedAtDesc(
        List<com.lms.lms.model.Course> courses, 
        org.springframework.data.domain.Pageable pageable
    );

    List<CourseDiscussion> findByCourseInAndCreatedAtAfterOrderByCreatedAtDesc(
        List<com.lms.lms.model.Course> courses, 
        java.time.LocalDateTime after
    );
}
