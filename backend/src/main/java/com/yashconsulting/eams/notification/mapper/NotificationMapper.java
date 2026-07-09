package com.yashconsulting.eams.notification.mapper;

import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.dto.NotificationResponse;
import com.yashconsulting.eams.notification.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Notification.builder()
                .title(request.getTitle() != null ? request.getTitle().trim() : null)
                .message(request.getMessage() != null ? request.getMessage().trim() : null)
                .notificationType(request.getNotificationType())
                .priority(request.getPriority())
                .recipientUserId(request.getRecipientUserId())
                .referenceType(request.getReferenceType() != null ? request.getReferenceType().trim() : null)
                .referenceId(request.getReferenceId())
                .read(false)
                .active(true)
                .build();
    }

    public NotificationResponse toResponse(Notification entity) {
        if (entity == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .notificationType(entity.getNotificationType())
                .priority(entity.getPriority())
                .recipientUserId(entity.getRecipientUserId())
                .read(entity.isRead())
                .readAt(entity.getReadAt())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public List<NotificationResponse> toResponseList(List<Notification> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
