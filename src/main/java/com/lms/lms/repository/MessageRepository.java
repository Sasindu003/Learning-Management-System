package com.lms.lms.repository;

import com.lms.lms.model.Message;
import com.lms.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverAndDeletedByReceiverFalseOrderBySentAtDesc(User receiver);

    List<Message> findBySenderAndDeletedBySenderFalseOrderBySentAtDesc(User sender);

    long countByReceiverAndReadFalseAndDeletedByReceiverFalse(User receiver);
}
