package com.yashconsulting.eams.email.service;

import java.util.Map;

/**
 * Reusable enterprise email service supporting HTML templates and async delivery.
 */
public interface EmailService {

    /**
     * Send an HTML email using a Thymeleaf template.
     *
     * @param to           recipient email address
     * @param subject      email subject line
     * @param templateName Thymeleaf template name (without path/extension)
     * @param variables    template variables
     */
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables);

    void sendWelcomeEmail(String to, String firstName);

    void sendPasswordResetEmail(String to, String firstName, String resetLink);

    void sendMaintenanceReminderEmail(String to, String planCode, String planName, String nextDate);

    void sendPurchaseOrderApprovalEmail(String to, String poNumber);

    void sendLowInventoryAlertEmail(String to, String partNumber, String partName, int currentStock, int minimumStock);

    void sendWorkOrderAssignmentEmail(String to, String workOrderNumber, String description);
}
