package com.hirehub.hirehub_backend.service;

public interface EmailService {
    void sendNotificationEmail(String toEmail, String subject, String body) throws Exception;
    void sendApplicationStatusEmail(String toEmail, String jobTitle, String status) throws Exception;
    void sendNewJobAlertEmail(String toEmail, String jobTitle, String jobUrl) throws Exception;
}


