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
    private final FileStorageService fileStorageService;

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
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));

        // Collect files to delete
        List<String> filesToDelete = new ArrayList<>();

        // Materials files
        if (course.getMaterials() != null) {
            for (CourseMaterial material : course.getMaterials()) {
                if (material.getFilePath() != null) {
                    filesToDelete.add(material.getFilePath());
                }
            }
        }

        // Assignment attachments and submissions
        if (course.getAssignments() != null) {
            for (Assignment assignment : course.getAssignments()) {
                if (assignment.getAttachmentPath() != null) {
                    filesToDelete.add(assignment.getAttachmentPath());
                }
                if (assignment.getSubmissions() != null) {
                    for (AssignmentSubmission submission : assignment.getSubmissions()) {
                        if (submission.getFilePath() != null) {
                            filesToDelete.add(submission.getFilePath());
                        }
                    }
                }
            }
        }

        // Delete records from DB (cascaded)
        courseRepository.delete(course);

        // Delete physical files
        for (String path : filesToDelete) {
            try {
                fileStorageService.delete(path);
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Failed to delete file: " + path + ". Error: " + e.getMessage());
            }
        }
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

    public Optional<CourseMaterial> findMaterialById(Long id) {
        return materialRepository.findById(id);
    }
}
