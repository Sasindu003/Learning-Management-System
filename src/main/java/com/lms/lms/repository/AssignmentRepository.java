package com.lms.lms.repository;

import com.lms.lms.model.Assignment;
import com.lms.lms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseOrderByDueDateDesc(Course course);

    List<Assignment> findByCourseIn(List<Course> courses);
}
