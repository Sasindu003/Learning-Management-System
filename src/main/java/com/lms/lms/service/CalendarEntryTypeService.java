package com.lms.lms.service;

import com.lms.lms.model.*;
import com.lms.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarEntryTypeService {

    private final CalendarEntryTypeRepository repository;

    public List<CalendarEntryType> findAll() {
        return repository.findAll();
    }

    public Optional<CalendarEntryType> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public CalendarEntryType save(CalendarEntryType type) {
        return repository.save(type);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
