package com.lms.lms.repository;

import com.lms.lms.model.CalendarEntryType;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEntryTypeRepository extends JpaRepository<CalendarEntryType, Long> {
    Optional<CalendarEntryType> findByName(String name);
    List<CalendarEntryType> findByOwnerIsNullOrOwner(User owner);
}
