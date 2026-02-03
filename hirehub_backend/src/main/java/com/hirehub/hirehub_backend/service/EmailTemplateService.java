package com.hirehub.hirehub_backend.service;

public interface EmailTemplateService {
    // Application status change email
    String getApplicationStatusChangeTemplate(String candidateName, String jobTitle, String companyName, String status, String interviewTime);

    // New job alert email
    String getNewJobAlertTemplate(String userName, String alertName, int jobCount, String jobList);

    // Interview scheduled email
    String getInterviewScheduledTemplate(String candidateName, String jobTitle, String companyName, String scheduledAt, String location, String calendarLink);

    // Interview cancelled email
    String getInterviewCancelledTemplate(String recipientName, String jobTitle, String companyName, String scheduledAt, String reason);

    // Interview rescheduled email
    String getInterviewRescheduledTemplate(String recipientName, String jobTitle, String companyName, String oldDateTime, String newDateTime, String reason);

    // New message email
    String getNewMessageTemplate(String recipientName, String senderName, String messagePreview, String conversationLink);

    // Welcome email
    String getWelcomeEmailTemplate(String userName, String role);

    // Password reset email
    String getPasswordResetTemplate(String userName, String resetLink);
}

