package com.lms.lms.repository;

import com.lms.lms.model.Timetable;
import com.lms.lms.model.Grade;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByGradeOrderByDayOfWeekAscStartTimeAsc(Grade grade);

    List<Timetable> findByTeacherOrderByDayOfWeekAscStartTimeAsc(User teacher);
}
