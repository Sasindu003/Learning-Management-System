package com.lms.lms.repository;

import com.lms.lms.model.Course;
import com.lms.lms.model.Grade;
import com.lms.lms.model.User;
import com.lms.lms.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacher(User teacher);

    List<Course> findByGrade(Grade grade);

    List<Course> findBySubject(Subject subject);

    List<Course> findByTeacherAndActive(User teacher, boolean active);

    List<Course> findByGradeAndActive(Grade grade, boolean active);

    long countByTeacher(User teacher);
}
