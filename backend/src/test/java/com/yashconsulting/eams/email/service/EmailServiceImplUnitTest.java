package com.yashconsulting.eams.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplUnitTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender, templateEngine);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@eams.com");
        ReflectionTestUtils.setField(emailService, "fromName", "EAMS System");
    }

    @Test
    void sendHtmlEmail_validParams_sendsEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Hello</html>");

        assertDoesNotThrow(() ->
                emailService.sendHtmlEmail("user@example.com", "Test Subject", "welcome", Map.of("firstName", "John"))
        );

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("email/welcome"), any(Context.class));
    }

    @Test
    void sendWelcomeEmail_callsSendHtmlEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Welcome</html>");

        assertDoesNotThrow(() -> emailService.sendWelcomeEmail("user@example.com", "John"));

        verify(templateEngine).process(eq("email/welcome"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_callsSendHtmlEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Reset</html>");

        assertDoesNotThrow(() ->
                emailService.sendPasswordResetEmail("user@example.com", "John", "https://reset-link"));

        verify(templateEngine).process(eq("email/password-reset"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendMaintenanceReminderEmail_callsSendHtmlEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Reminder</html>");

        assertDoesNotThrow(() ->
                emailService.sendMaintenanceReminderEmail("admin@example.com", "MP-001", "Monthly Check", "2026-08-01"));

        verify(templateEngine).process(eq("email/maintenance-reminder"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendPurchaseOrderApprovalEmail_callsSendHtmlEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Approved</html>");

        assertDoesNotThrow(() ->
                emailService.sendPurchaseOrderApprovalEmail("user@example.com", "PO-2026-001"));

        verify(templateEngine).process(eq("email/purchase-order-approval"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendLowInventoryAlertEmail_callsSendHtmlEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Alert</html>");

        assertDoesNotThrow(() ->
                emailService.sendLowInventoryAlertEmail("admin@example.com", "SP-001", "Filter", 3, 10));

        verify(templateEngine).process(eq("email/low-inventory-alert"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendWorkOrderAssignmentEmail_callsSendHtmlEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Assigned</html>");

        assertDoesNotThrow(() ->
                emailService.sendWorkOrderAssignmentEmail("tech@example.com", "WO-001", "Fix pump"));

        verify(templateEngine).process(eq("email/work-order-assignment"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendWorkOrderAssignmentEmail_nullDescription_handledGracefully() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Assigned</html>");

        assertDoesNotThrow(() ->
                emailService.sendWorkOrderAssignmentEmail("tech@example.com", "WO-001", null));

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlEmail_mailSenderThrows_propagatesAsRuntimeException() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () ->
                emailService.sendHtmlEmail("user@example.com", "Subject", "welcome", Map.of()));
    }
}
