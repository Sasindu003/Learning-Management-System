package com.lms.lms.repository;

import com.lms.lms.model.StudentGrade;
import com.lms.lms.model.User;
import com.lms.lms.model.Subject;
import com.lms.lms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByStudent(User student);

    List<StudentGrade> findByStudentAndTerm(User student, String term);

    List<StudentGrade> findByStudentAndSubject(User student, Subject subject);

    List<StudentGrade> findByCourse(Course course);
}
