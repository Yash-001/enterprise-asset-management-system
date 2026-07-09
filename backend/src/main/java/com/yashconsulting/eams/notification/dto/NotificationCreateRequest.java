package com.yashconsulting.eams.notification.dto;

import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload request for creating a new notification")
public class NotificationCreateRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    @Schema(description = "Notification title summary", example = "New Work Order Assigned")
    private String title;

    @NotBlank(message = "Message must not be blank")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    @Schema(description = "Detailed notification description body", example = "Work order WO-2026-001 has been assigned to you.")
    private String message;

    @NotNull(message = "Notification type must not be null")
    @Schema(description = "Context module generating the notification", example = "WORK_ORDER")
    private NotificationType notificationType;

    @NotNull(message = "Priority must not be null")
    @Schema(description = "Priority level of the notification urgency", example = "HIGH")
    private NotificationPriority priority;

    @NotNull(message = "Recipient user ID must not be null")
    @Schema(description = "Target user ID receiving the notification", example = "2")
    private Long recipientUserId;

    @Size(max = 100, message = "Reference type must not exceed 100 characters")
    @Schema(description = "Entity table type for linkage (optional)", example = "WORK_ORDER")
    private String referenceType;

    @Schema(description = "Target reference entity primary key ID (optional)", example = "15")
    private Long referenceId;
}
