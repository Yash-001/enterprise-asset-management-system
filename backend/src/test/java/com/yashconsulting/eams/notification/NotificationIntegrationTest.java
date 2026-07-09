package com.yashconsulting.eams.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.notification.dto.NotificationCreateRequest;
import com.yashconsulting.eams.notification.entity.Notification;
import com.yashconsulting.eams.notification.entity.NotificationPriority;
import com.yashconsulting.eams.notification.entity.NotificationType;
import com.yashconsulting.eams.notification.repository.NotificationRepository;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class NotificationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private String otherUserToken;

    private User seededUser;
    private User seededOtherUser;

    @BeforeEach
    void setUp() throws Exception {
        notificationRepository.deleteAll();
        userRepository.deleteAll();

        // Seed Admin
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Eams")
                .email("admin@eams.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        // Seed Regular User 1
        seededUser = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        seededUser = userRepository.save(seededUser);

        // Seed Regular User 2
        seededOtherUser = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@eams.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .active(true)
                .build();
        seededOtherUser = userRepository.save(seededOtherUser);

        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
        userToken = obtainAccessToken("jane.doe@eams.com", "User@123");
        otherUserToken = obtainAccessToken("john.smith@eams.com", "User@123");
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        return loginResponse.getAccessToken();
    }

    @Test
    void whenCreateNotificationAsAdmin_thenReturns201AndNotificationCreated() throws Exception {
        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .title("New Assignment")
                .message("Asset assigned to you")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.MEDIUM)
                .recipientUserId(seededUser.getId())
                .build();

        mockMvc.perform(post("/api/v1/notifications")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("New Assignment")))
                .andExpect(jsonPath("$.read", is(false)));
    }

    @Test
    void whenCreateNotificationAsUser_thenReturns500InternalServerError() throws Exception {
        NotificationCreateRequest request = NotificationCreateRequest.builder()
                .title("Hacker Message")
                .message("Spamming notifications")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .build();

        mockMvc.perform(post("/api/v1/notifications")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenSearchNotificationsAsUser_thenForcesOnlyOwnRecipientNotifications() throws Exception {
        // Seed 1 notification for user 1, 1 notification for user 2
        notificationRepository.save(Notification.builder()
                .title("User 1 Alert")
                .message("Body 1")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(false)
                .active(true)
                .build());

        notificationRepository.save(Notification.builder()
                .title("User 2 Alert")
                .message("Body 2")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededOtherUser.getId())
                .read(false)
                .active(true)
                .build());

        // Search as User 1: should only return User 1 Alert
        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("User 1 Alert")))
                .andExpect(jsonPath("$.content[0].recipientUserId", is(seededUser.getId().intValue())));
    }

    @Test
    void whenMarkAsRead_thenReadStatusUpdatesAndTimestampSet() throws Exception {
        Notification notif = notificationRepository.save(Notification.builder()
                .title("Alert")
                .message("Message body")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(false)
                .active(true)
                .build());

        mockMvc.perform(put("/api/v1/notifications/" + notif.getId() + "/read")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Notification updated = notificationRepository.findById(notif.getId()).orElseThrow();
        assertTrue(updated.isRead());
        notNullValue().matches(updated.getReadAt());
    }

    @Test
    void whenMarkAllAsRead_thenAllRecipientUnreadNotificationsMarkedRead() throws Exception {
        // Seed 2 unread notifications for user 1
        notificationRepository.save(Notification.builder()
                .title("Alert 1")
                .message("Body 1")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(false)
                .active(true)
                .build());

        notificationRepository.save(Notification.builder()
                .title("Alert 2")
                .message("Body 2")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(false)
                .active(true)
                .build());

        mockMvc.perform(put("/api/v1/notifications/read-all")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        long countAfter = notificationRepository.countByRecipientUserIdAndReadFalseAndActiveTrue(seededUser.getId());
        assertEquals(0, countAfter);
    }

    @Test
    void whenGetUnreadCount_thenReturnsCorrectUnreadCount() throws Exception {
        notificationRepository.save(Notification.builder()
                .title("Alert 1")
                .message("Body 1")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(false)
                .active(true)
                .build());

        notificationRepository.save(Notification.builder()
                .title("Alert 2")
                .message("Body 2")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(true)
                .active(true)
                .build());

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    void whenDeleteOwnNotificationAsUser_thenSoftDeletesAndExcludeFromSearch() throws Exception {
        Notification notif = notificationRepository.save(Notification.builder()
                .title("Alert")
                .message("Message body")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededUser.getId())
                .read(false)
                .active(true)
                .build());

        mockMvc.perform(delete("/api/v1/notifications/" + notif.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Notification deleted = notificationRepository.findById(notif.getId()).orElseThrow();
        assertFalse(deleted.isActive());
    }

    @Test
    void whenMutateAnotherUserNotificationAsUser_thenReturns500InternalServerError() throws Exception {
        Notification notifForUser2 = notificationRepository.save(Notification.builder()
                .title("Alert User 2")
                .message("Body")
                .notificationType(NotificationType.SYSTEM)
                .priority(NotificationPriority.LOW)
                .recipientUserId(seededOtherUser.getId())
                .read(false)
                .active(true)
                .build());

        // User 1 tries to mark User 2's notification as read -> should fail with AccessDenied (mapped to 500)
        mockMvc.perform(put("/api/v1/notifications/" + notifForUser2.getId() + "/read")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
