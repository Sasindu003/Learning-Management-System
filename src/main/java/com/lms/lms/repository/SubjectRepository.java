package com.lms.lms.repository;

import com.lms.lms.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);

    Optional<Subject> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);
}
