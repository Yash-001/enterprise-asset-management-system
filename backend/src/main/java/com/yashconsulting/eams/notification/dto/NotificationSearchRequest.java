package com.yashconsulting.eams.notification.dto;

import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request criteria payload for searching notifications dynamically with pagination")
public class NotificationSearchRequest {

    @Schema(description = "Filter by title (contains, case-insensitive)", example = "Assigned")
    private String title;

    @Schema(description = "Filter by recipient user ID", example = "2")
    private Long recipientUserId;

    @Schema(description = "Filter by read status status", example = "false")
    private Boolean read;

    @Schema(description = "Filter by notification type", example = "WORK_ORDER")
    private NotificationType notificationType;

    @Schema(description = "Filter by priority level", example = "HIGH")
    private NotificationPriority priority;

    @Schema(description = "Filter by exact reference type", example = "WORK_ORDER")
    private String referenceType;

    @Schema(description = "Filter by exact reference ID", example = "15")
    private Long referenceId;

    @Schema(description = "Zero-indexed page number for pagination retrieval", example = "0")
    @Min(value = 0, message = "Page number must be zero or positive")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Number of records per page (size limit)", example = "20")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Builder.Default
    private Integer size = 20;

    @Schema(description = "Database column property name to sort results by", example = "id")
    @Builder.Default
    private String sortBy = "id";

    @Schema(description = "Sorting order direction ('ASC' or 'DESC')", example = "DESC")
    @Builder.Default
    private String sortDirection = "DESC";
}
