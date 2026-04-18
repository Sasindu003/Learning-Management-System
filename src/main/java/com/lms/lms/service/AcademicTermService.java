package com.lms.lms.service;

import com.lms.lms.model.AcademicTerm;
import com.lms.lms.repository.AcademicTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AcademicTermService {
    private final AcademicTermRepository termRepository;

    public List<AcademicTerm> findAll() {
        return termRepository.findAllByOrderByStartDateDesc();
    }

    public Optional<AcademicTerm> findById(Long id) {
        return termRepository.findById(id);
    }

    public Optional<AcademicTerm> findActive() {
        return termRepository.findByActiveTrue();
    }

    @Transactional
    public AcademicTerm save(AcademicTerm term) {
        if (term.isActive()) {
            findActive().ifPresent(t -> {
                // Only deactivate if it's a different term
                if (term.getId() == null || !t.getId().equals(term.getId())) {
                    t.setActive(false);
                    termRepository.save(t);
                }
            });
        }
        return termRepository.save(term);
    }

    @Transactional
    public void delete(Long id) {
        termRepository.deleteById(id);
    }
}
