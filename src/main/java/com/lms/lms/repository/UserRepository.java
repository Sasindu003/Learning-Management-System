package com.lms.lms.repository;

import com.lms.lms.model.User;
import com.lms.lms.model.User.Role;
import com.lms.lms.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByPrimaryId(String primaryId);

    List<User> findByRole(Role role);

    List<User> findByRoleAndEnabled(Role role, boolean enabled);

    List<User> findByGrade(Grade grade);

    List<User> findByRoleAndGrade(Role role, Grade grade);

    long countByRole(Role role);

    boolean existsByUsername(String username);

    boolean existsByPrimaryId(String primaryId);
}
