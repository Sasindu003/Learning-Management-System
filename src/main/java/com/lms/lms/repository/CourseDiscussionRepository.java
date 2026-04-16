package com.lms.lms.repository;

import com.lms.lms.model.CourseDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CourseDiscussionRepository extends JpaRepository<CourseDiscussion, Long> {
    List<CourseDiscussion> findByCourseIdOrderByCreatedAtAsc(Long courseId);
    
    org.springframework.data.domain.Page<CourseDiscussion> findByCourseInOrderByCreatedAtDesc(
        List<com.lms.lms.model.Course> courses, 
        org.springframework.data.domain.Pageable pageable
    );

    @org.springframework.data.jpa.repository.Query("SELECT d FROM CourseDiscussion d WHERE d.course.teacher = :teacher AND d.sender.grade IN :grades ORDER BY d.createdAt DESC")
    List<com.lms.lms.model.CourseDiscussion> findRecentByTeacherAndGrades(
        @org.springframework.data.repository.query.Param("teacher") com.lms.lms.model.User teacher, 
        @org.springframework.data.repository.query.Param("grades") Set<com.lms.lms.model.Grade> grades, 
        org.springframework.data.domain.Pageable pageable
    );
}
