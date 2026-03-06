package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }

    public boolean existsByName(String name) {
        return gradeRepository.existsByName(name);
    }

    @Transactional
    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Transactional
    public void delete(Long id) {
        gradeRepository.deleteById(id);
    }
}
