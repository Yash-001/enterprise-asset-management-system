package com.yashconsulting.eams.notification.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.dto.NotificationResponse;
import com.yashconsulting.eams.notification.entity.Notification;
import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import com.yashconsulting.eams.notification.mapper.NotificationMapper;
import com.yashconsulting.eams.notification.repository.NotificationRepository;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplUnitTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotification_validRecipient_succeeds() {
        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .title("Test Notification")
                .message("Test message")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.MEDIUM)
                .recipientUserId(1L)
                .build();

        Notification entity = Notification.builder().id(1L).title("Test Notification").build();
        NotificationResponse response = NotificationResponse.builder().id(1L).title("Test Notification").build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationMapper.toEntity(request)).thenReturn(entity);
        when(notificationRepository.save(entity)).thenReturn(entity);
        when(notificationMapper.toResponse(entity)).thenReturn(response);

        NotificationResponse result = notificationService.createNotification(request);

        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        verify(notificationRepository).save(entity);
    }

    @Test
    void createNotification_invalidRecipient_throwsResourceNotFoundException() {
        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .recipientUserId(99L)
                .build();

        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.createNotification(request));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void getNotificationById_existing_returnsResponse() {
        Notification entity = Notification.builder().id(1L).title("Alert").build();
        NotificationResponse response = NotificationResponse.builder().id(1L).title("Alert").build();

        when(notificationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(entity));
        when(notificationMapper.toResponse(entity)).thenReturn(response);

        NotificationResponse result = notificationService.getNotificationById(1L);

        assertNotNull(result);
        assertEquals("Alert", result.getTitle());
    }

    @Test
    void getNotificationById_nonExisting_throwsResourceNotFoundException() {
        when(notificationRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(99L));
    }

    @Test
    void deleteNotification_existing_softDeletes() {
        Notification entity = Notification.builder().id(1L).active(true).build();

        when(notificationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(entity));

        notificationService.deleteNotification(1L);

        assertFalse(entity.isActive());
        verify(notificationRepository).save(entity);
    }

    @Test
    void markAsRead_unreadNotification_setsReadTrue() {
        Notification entity = Notification.builder().id(1L).read(false).active(true).build();

        when(notificationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(entity));

        notificationService.markAsRead(1L);

        assertTrue(entity.isRead());
        assertNotNull(entity.getReadAt());
        verify(notificationRepository).save(entity);
    }

    @Test
    void markAsRead_alreadyRead_doesNotSaveAgain() {
        Notification entity = Notification.builder()
                .id(1L).read(true).readAt(LocalDateTime.now()).active(true).build();

        when(notificationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(entity));

        notificationService.markAsRead(1L);

        // save is not called because it's already read
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAllAsRead_validUser_updatesAllUnread() {
        Notification n1 = Notification.builder().id(1L).read(false).build();
        Notification n2 = Notification.builder().id(2L).read(false).build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findAllByRecipientUserIdAndReadFalseAndActiveTrue(1L))
                .thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead(1L);

        assertTrue(n1.isRead());
        assertTrue(n2.isRead());
        assertNotNull(n1.getReadAt());
        assertNotNull(n2.getReadAt());
        verify(notificationRepository).saveAll(List.of(n1, n2));
    }

    @Test
    void markAllAsRead_invalidUser_throwsResourceNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAllAsRead(99L));
    }

    @Test
    void getUnreadCount_validUser_returnsCount() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.countByRecipientUserIdAndReadFalseAndActiveTrue(1L)).thenReturn(5L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(5L, count);
    }

    @Test
    void getUnreadCount_invalidUser_throwsResourceNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.getUnreadCount(99L));
    }
}
