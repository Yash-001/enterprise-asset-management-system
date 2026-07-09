package com.yashconsulting.eams.notification.service;

import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.dto.NotificationResponse;
import com.yashconsulting.eams.notification.dto.NotificationSearchRequest;
import org.springframework.data.domain.Page;

public interface NotificationService {

    NotificationResponse createNotification(NotificationCreateRequest request);

    Page<NotificationResponse> searchNotifications(NotificationSearchRequest request);

    NotificationResponse getNotificationById(Long id);

    void deleteNotification(Long id);

    void markAsRead(Long id);

    void markAllAsRead(Long recipientUserId);

    long getUnreadCount(Long recipientUserId);
}
