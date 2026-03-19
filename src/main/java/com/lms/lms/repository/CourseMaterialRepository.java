package com.lms.lms.repository;

import com.lms.lms.model.CourseMaterial;
import com.lms.lms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findByCourseOrderByUploadedAtDesc(Course course);

    long countByCourse(Course course);
}
