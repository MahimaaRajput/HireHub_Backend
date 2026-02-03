package com.hirehub.hirehub_backend.service;

public interface EmailService {
    void sendNotificationEmail(String toEmail, String subject, String body) throws Exception;
    void sendHtmlEmail(String toEmail, String subject, String htmlBody) throws Exception;
    void sendApplicationStatusEmail(String toEmail, String jobTitle, String status) throws Exception;
    void sendApplicationStatusEmailWithTemplate(String toEmail, String candidateName, String jobTitle, String companyName, String status, String interviewTime) throws Exception;
    void sendNewJobAlertEmail(String toEmail, String jobTitle, String jobUrl) throws Exception;
    void sendInterviewScheduledEmail(String toEmail, String candidateName, String jobTitle, String companyName, String scheduledAt, String location, String calendarLink) throws Exception;
    void sendInterviewCancelledEmail(String toEmail, String recipientName, String jobTitle, String companyName, String scheduledAt, String reason) throws Exception;
    void sendInterviewRescheduledEmail(String toEmail, String recipientName, String jobTitle, String companyName, String oldDateTime, String newDateTime, String reason) throws Exception;
    void sendNewMessageEmail(String toEmail, String recipientName, String senderName, String messagePreview, String conversationLink) throws Exception;
    void sendWelcomeEmail(String toEmail, String userName, String role) throws Exception;
    void sendPasswordResetEmail(String toEmail, String userName, String resetLink) throws Exception;
}



