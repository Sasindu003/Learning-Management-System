package com.lms.lms.repository;

import com.lms.lms.model.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, Long> {
    List<AcademicTerm> findByActiveTrue();

    List<AcademicTerm> findAllByOrderByStartDateDesc();

    @Query("SELECT t FROM AcademicTerm t WHERE (:id IS NULL OR t.id <> :id) AND (t.startDate <= :endDate AND t.endDate >= :startDate)")
    List<AcademicTerm> findOverlapping(@Param("id") Long id, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM AcademicTerm t WHERE :date BETWEEN t.startDate AND t.endDate")
    List<AcademicTerm> findByDate(@Param("date") LocalDate date);
}
