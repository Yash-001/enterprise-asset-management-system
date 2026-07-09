package com.yashconsulting.eams.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${eams.mail.from:noreply@eams.com}")
    private String fromAddress;

    @Value("${eams.mail.from-name:EAMS System}")
    private String fromName;

    @Override
    @Async
    @Retryable(
            retryFor = {MessagingException.class, RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        log.info("Sending email to: {} with subject: {}", to, subject);
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {} - {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {} - {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String firstName) {
        Map<String, Object> variables = Map.of(
                "firstName", firstName,
                "loginUrl", "https://eams.example.com/login"
        );
        sendHtmlEmail(to, "Welcome to EAMS", "welcome", variables);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String firstName, String resetLink) {
        Map<String, Object> variables = Map.of(
                "firstName", firstName,
                "resetLink", resetLink
        );
        sendHtmlEmail(to, "Password Reset Request", "password-reset", variables);
    }

    @Override
    @Async
    public void sendMaintenanceReminderEmail(String to, String planCode, String planName, String nextDate) {
        Map<String, Object> variables = Map.of(
                "planCode", planCode,
                "planName", planName,
                "nextDate", nextDate
        );
        sendHtmlEmail(to, "Maintenance Reminder: " + planCode, "maintenance-reminder", variables);
    }

    @Override
    @Async
    public void sendPurchaseOrderApprovalEmail(String to, String poNumber) {
        Map<String, Object> variables = Map.of(
                "poNumber", poNumber
        );
        sendHtmlEmail(to, "Purchase Order Approved: " + poNumber, "purchase-order-approval", variables);
    }

    @Override
    @Async
    public void sendLowInventoryAlertEmail(String to, String partNumber, String partName, int currentStock, int minimumStock) {
        Map<String, Object> variables = Map.of(
                "partNumber", partNumber,
                "partName", partName,
                "currentStock", currentStock,
                "minimumStock", minimumStock
        );
        sendHtmlEmail(to, "Low Inventory Alert: " + partNumber, "low-inventory-alert", variables);
    }

    @Override
    @Async
    public void sendWorkOrderAssignmentEmail(String to, String workOrderNumber, String description) {
        Map<String, Object> variables = Map.of(
                "workOrderNumber", workOrderNumber,
                "description", description != null ? description : ""
        );
        sendHtmlEmail(to, "Work Order Assigned: " + workOrderNumber, "work-order-assignment", variables);
    }
}
