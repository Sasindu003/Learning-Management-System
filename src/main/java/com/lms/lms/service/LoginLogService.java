package com.lms.lms.service;

import com.lms.lms.model.LoginLog;
import com.lms.lms.repository.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    public List<LoginLog> getAllLogs() {
        return loginLogRepository.findAllByOrderByTimestampDesc();
    }

    @Transactional
    public void save(LoginLog log) {
        loginLogRepository.save(log);
    }

    @Transactional
    public void deleteLog(Long id) {
        loginLogRepository.deleteById(id);
    }

    @Transactional
    public void clearAllLogs() {
        loginLogRepository.deleteAll();
    }
}
