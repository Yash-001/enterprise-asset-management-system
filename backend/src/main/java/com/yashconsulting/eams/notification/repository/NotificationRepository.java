package com.yashconsulting.eams.notification.repository;

import com.yashconsulting.eams.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    Optional<Notification> findByIdAndActiveTrue(Long id);

    long countByRecipientUserIdAndReadFalseAndActiveTrue(Long recipientUserId);

    List<Notification> findAllByRecipientUserIdAndReadFalseAndActiveTrue(Long recipientUserId);
}
