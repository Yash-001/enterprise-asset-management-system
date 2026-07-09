package com.yashconsulting.eams.notification.dto;

import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload carrying notification details")
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationType notificationType;
    private NotificationPriority priority;
    private Long recipientUserId;
    private boolean read;
    private LocalDateTime readAt;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime createdAt;
    private String createdBy;
}
