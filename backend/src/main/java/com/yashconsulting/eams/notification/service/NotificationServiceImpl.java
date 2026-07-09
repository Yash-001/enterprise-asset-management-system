package com.yashconsulting.eams.notification.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.dto.NotificationResponse;
import com.yashconsulting.eams.notification.dto.NotificationSearchRequest;
import com.yashconsulting.eams.notification.entity.Notification;
import com.yashconsulting.eams.notification.mapper.NotificationMapper;
import com.yashconsulting.eams.notification.repository.NotificationRepository;
import com.yashconsulting.eams.notification.specification.NotificationSpecification;
import com.yashconsulting.eams.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        log.info("Creating notification: '{}' for recipient user ID: {}", request.getTitle(), request.getRecipientUserId());

        if (!userRepository.existsById(request.getRecipientUserId())) {
            throw new ResourceNotFoundException("Recipient user not found with ID: " + request.getRecipientUserId());
        }

        Notification entity = notificationMapper.toEntity(request);
        Notification saved = notificationRepository.save(entity);
        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> searchNotifications(NotificationSearchRequest request) {
        log.info("Searching notifications dynamically");

        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Specification<Notification> spec = NotificationSpecification.getSpecification(request);

        return notificationRepository.findAll(spec, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.info("Fetching notification by ID: {}", id);
        Notification entity = notificationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
        return notificationMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        log.info("Soft deleting notification with ID: {}", id);
        Notification entity = notificationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
        entity.setActive(false);
        notificationRepository.save(entity);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        log.info("Marking notification with ID: {} as read", id);
        Notification entity = notificationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
        if (!entity.isRead()) {
            entity.setRead(true);
            entity.setReadAt(LocalDateTime.now());
            notificationRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long recipientUserId) {
        log.info("Marking all notifications for recipient user ID: {} as read", recipientUserId);
        if (!userRepository.existsById(recipientUserId)) {
            throw new ResourceNotFoundException("Recipient user not found with ID: " + recipientUserId);
        }

        List<Notification> unread = notificationRepository.findAllByRecipientUserIdAndReadFalseAndActiveTrue(recipientUserId);
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientUserId) {
        log.info("Fetching unread notification count for recipient user ID: {}", recipientUserId);
        if (!userRepository.existsById(recipientUserId)) {
            throw new ResourceNotFoundException("Recipient user not found with ID: " + recipientUserId);
        }
        return notificationRepository.countByRecipientUserIdAndReadFalseAndActiveTrue(recipientUserId);
    }
}
