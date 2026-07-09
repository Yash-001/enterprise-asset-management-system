package com.yashconsulting.eams.notification.controller;

import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.dto.NotificationResponse;
import com.yashconsulting.eams.notification.dto.NotificationSearchRequest;
import com.yashconsulting.eams.notification.service.NotificationService;
import com.yashconsulting.eams.security.CustomUserDetails;
import com.yashconsulting.eams.security.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for sending, tracking, searching, and marking user notifications as read")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new notification", description = "Admin or Manager sends a notification to a specific recipient user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Recipient user not found")
    })
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationCreateRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search notifications dynamically", description = "Retrieves a paginated list of active notifications. Regular users are forced to view only their own notifications.")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
            @Valid NotificationSearchRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getUser().getRole() == Role.USER) {
            request.setRecipientUserId(userDetails.getUser().getId());
        }
        return ResponseEntity.ok(notificationService.searchNotifications(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get notification details by ID", description = "Retrieves single notification. Regular users can only read their own notifications.")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationResponse response = notificationService.getNotificationById(id);
        if (userDetails.getUser().getRole() == Role.USER && !response.getRecipientUserId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("Access Denied: Cannot view notifications of another user");
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Soft delete notification by ID", description = "Removes notification by setting active to false. Regular users can only delete their own notifications.")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationResponse response = notificationService.getNotificationById(id);
        if (userDetails.getUser().getRole() == Role.USER && !response.getRecipientUserId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("Access Denied: Cannot delete notifications of another user");
        }
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Mark single notification as read", description = "Updates status to read and sets read timestamp. Regular users are restricted to their own notifications.")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationResponse response = notificationService.getNotificationById(id);
        if (userDetails.getUser().getRole() == Role.USER && !response.getRecipientUserId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("Access Denied: Cannot mark notifications of another user as read");
        }
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Mark all notifications for a user as read", description = "Updates read status for all unread notifications of a recipient. Regular users are forced to mark their own.")
    public ResponseEntity<Void> markAllAsRead(
            @RequestParam(required = false) Long recipientUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long targetUserId = recipientUserId;
        if (userDetails.getUser().getRole() == Role.USER || targetUserId == null) {
            targetUserId = userDetails.getUser().getId();
        }
        notificationService.markAllAsRead(targetUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get unread notifications count", description = "Returns total count of unread notifications for a recipient user.")
    public ResponseEntity<Long> getUnreadCount(
            @RequestParam(required = false) Long recipientUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long targetUserId = recipientUserId;
        if (userDetails.getUser().getRole() == Role.USER || targetUserId == null) {
            targetUserId = userDetails.getUser().getId();
        }
        long count = notificationService.getUnreadCount(targetUserId);
        return ResponseEntity.ok(count);
    }
}
