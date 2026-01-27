package com.hirehub.hirehub_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    private static final String FROM_EMAIL = "noreply@hirehub.com";
    
    @Override
    public void sendNotificationEmail(String toEmail, String subject, String body) throws Exception {
        if (mailSender == null) {
            // If mail sender is not configured, just log (for development)
            System.out.println("Email would be sent to: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }
    
    @Override
    public void sendApplicationStatusEmail(String toEmail, String jobTitle, String status) throws Exception {
        String subject = "Application Status Update - " + jobTitle;
        String body = "Dear Applicant,\n\n" +
                "Your application status for the position \"" + jobTitle + "\" has been updated to: " + status + "\n\n" +
                "Please log in to your account to view more details.\n\n" +
                "Best regards,\nHireHub Team";
        sendNotificationEmail(toEmail, subject, body);
    }
    
    @Override
    public void sendNewJobAlertEmail(String toEmail, String jobTitle, String jobUrl) throws Exception {
        String subject = "New Job Alert - " + jobTitle;
        String body = "Dear Job Seeker,\n\n" +
                "We found a new job that matches your profile:\n" +
                "Job Title: " + jobTitle + "\n" +
                "View Job: " + jobUrl + "\n\n" +
                "Best regards,\nHireHub Team";
        sendNotificationEmail(toEmail, subject, body);
    }
}

