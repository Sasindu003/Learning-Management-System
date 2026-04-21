package com.lms.lms.repository;

import com.lms.lms.model.LoginLog;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findAllByOrderByTimestampDesc();
    List<LoginLog> findFirst5ByUserOrderByTimestampDesc(User user);
}
