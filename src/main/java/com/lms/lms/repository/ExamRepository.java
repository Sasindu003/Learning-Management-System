package com.lms.lms.repository;

import com.lms.lms.model.Exam;
import com.lms.lms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCourseOrderByExamDateDesc(Course course);

    List<Exam> findByCourseInAndPublished(List<Course> courses, boolean published);

    List<Exam> findByCourseIn(List<Course> courses);
}
