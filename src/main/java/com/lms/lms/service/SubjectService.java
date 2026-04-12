package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public boolean existsByName(String name) {
        return subjectRepository.existsByName(name);
    }

    public Optional<Subject> findByName(String name) {
        return subjectRepository.findByName(name);
    }

    public Optional<Subject> findByCode(String code) {
        return subjectRepository.findByCode(code);
    }

    public boolean existsByCode(String code) {
        return subjectRepository.existsByCode(code);
    }

    @Transactional
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Transactional
    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }
}
