package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMaterialRepository materialRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> findByTeacher(User teacher) {
        return courseRepository.findByTeacher(teacher);
    }

    public List<Course> findByGrade(Grade grade) {
        return courseRepository.findByGradeAndActive(grade, true);
    }

    public long countByTeacher(User teacher) {
        return courseRepository.countByTeacher(teacher);
    }

    @Transactional
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    // Materials
    public List<CourseMaterial> getMaterials(Course course) {
        return materialRepository.findByCourseOrderByUploadedAtDesc(course);
    }

    @Transactional
    public CourseMaterial saveMaterial(CourseMaterial material) {
        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }
}
