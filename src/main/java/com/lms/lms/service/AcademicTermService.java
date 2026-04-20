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

    public List<String> validate(AcademicTerm term) {
        List<String> errors = new ArrayList<>();

        if (term.getStartDate() == null || term.getEndDate() == null) {
            errors.add("Start and end dates are required.");
            return errors;
        }

        if (term.getEndDate().isBefore(term.getStartDate())) {
            errors.add("End date must be after or equal to start date.");
        }

        List<AcademicTerm> overlapping = termRepository.findOverlapping(term.getId(), term.getStartDate(), term.getEndDate());
        if (!overlapping.isEmpty()) {
            AcademicTerm first = overlapping.get(0);
            errors.add(String.format("Term period overlaps with '%s' (%s to %s)",
                first.getName(), first.getStartDate(), first.getEndDate()));
        }

        return errors;
    }
}
