package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentGradeService {

    private final StudentGradeRepository gradeRepository;

    public List<StudentGrade> findByStudent(User student) {
        return gradeRepository.findByStudent(student);
    }

    public List<StudentGrade> findByStudentAndTerm(User student, String term) {
        return gradeRepository.findByStudentAndTerm(student, term);
    }

    public List<StudentGrade> findByCourse(Course course) {
        return gradeRepository.findByCourse(course);
    }

    @Transactional
    public StudentGrade save(StudentGrade grade) {
        grade.calculatePercentage();
        grade.calculateLetterGrade();
        return gradeRepository.save(grade);
    }

    @Transactional
    public void delete(Long id) {
        gradeRepository.deleteById(id);
    }

    public double calculateGPA(User student) {
        List<StudentGrade> grades = gradeRepository.findByStudent(student);
        if (grades.isEmpty())
            return 0.0;
        double total = grades.stream().mapToDouble(StudentGrade::getPercentage).sum();
        return total / grades.size();
    }
}
