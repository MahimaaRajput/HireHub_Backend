package com.hirehub.hirehub_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
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
    public void sendHtmlEmail(String toEmail, String subject, String htmlBody) throws Exception {
        if (mailSender == null) {
            // If mail sender is not configured, just log (for development)
            System.out.println("HTML Email would be sent to: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("HTML Body: " + htmlBody);
            return;
        }
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(FROM_EMAIL);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true indicates HTML content
        
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
    public void sendApplicationStatusEmailWithTemplate(String toEmail, String candidateName, String jobTitle, String companyName, String status, String interviewTime) throws Exception {
        String subject = "Application Status Update - " + jobTitle;
        String htmlBody = emailTemplateService.getApplicationStatusChangeTemplate(candidateName, jobTitle, companyName, status, interviewTime);
        sendHtmlEmail(toEmail, subject, htmlBody);
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
    
    @Override
    public void sendInterviewScheduledEmail(String toEmail, String candidateName, String jobTitle, String companyName, String scheduledAt, String location, String calendarLink) throws Exception {
        String subject = "Interview Scheduled - " + jobTitle;
        String htmlBody = emailTemplateService.getInterviewScheduledTemplate(candidateName, jobTitle, companyName, scheduledAt, location, calendarLink);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    @Override
    public void sendInterviewCancelledEmail(String toEmail, String recipientName, String jobTitle, String companyName, String scheduledAt, String reason) throws Exception {
        String subject = "Interview Cancelled - " + jobTitle;
        String htmlBody = emailTemplateService.getInterviewCancelledTemplate(recipientName, jobTitle, companyName, scheduledAt, reason);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    @Override
    public void sendInterviewRescheduledEmail(String toEmail, String recipientName, String jobTitle, String companyName, String oldDateTime, String newDateTime, String reason) throws Exception {
        String subject = "Interview Rescheduled - " + jobTitle;
        String htmlBody = emailTemplateService.getInterviewRescheduledTemplate(recipientName, jobTitle, companyName, oldDateTime, newDateTime, reason);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    @Override
    public void sendNewMessageEmail(String toEmail, String recipientName, String senderName, String messagePreview, String conversationLink) throws Exception {
        String subject = "New Message from " + senderName;
        String htmlBody = emailTemplateService.getNewMessageTemplate(recipientName, senderName, messagePreview, conversationLink);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    @Override
    public void sendWelcomeEmail(String toEmail, String userName, String role) throws Exception {
        String subject = "Welcome to HireHub!";
        String htmlBody = emailTemplateService.getWelcomeEmailTemplate(userName, role);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
    
    @Override
    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink) throws Exception {
        String subject = "HireHub - Password Reset Request";
        String htmlBody = emailTemplateService.getPasswordResetTemplate(userName, resetLink);
        sendHtmlEmail(toEmail, subject, htmlBody);
    }
}



