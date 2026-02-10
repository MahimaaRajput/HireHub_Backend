package com.hirehub.hirehub_backend.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Override
    public String getApplicationStatusChangeTemplate(String candidateName, String jobTitle, String companyName, String status, String interviewTime) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#4CAF50;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - Application Status Update</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(candidateName).append(",</p>\n");
        template.append("<p>Your application status for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong> has been updated.</p>\n");
        template.append("<p><strong>New Status:</strong> ").append(status).append("</p>\n");
        if (interviewTime != null && !interviewTime.isEmpty()) {
            template.append("<p><strong>Interview Scheduled:</strong> ").append(interviewTime).append("</p>\n");
        }
        template.append("<p>Please log in to your HireHub account to view more details.</p>\n");
        template.append("<p>Thank you for your interest.</p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getNewJobAlertTemplate(String userName, String alertName, int jobCount, String jobList) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#2196F3;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".job-item{padding:10px;margin:10px 0;background-color:white;border-left:4px solid #2196F3;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - New Jobs Matching Your Alert</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(userName).append(",</p>\n");
        template.append("<p>We found <strong>").append(jobCount).append("</strong> new job(s) matching your job alert: <strong>").append(alertName).append("</strong></p>\n");
        template.append("<div>").append(jobList).append("</div>\n");
        template.append("<p>Log in to your HireHub account to view and apply for these positions.</p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getInterviewScheduledTemplate(String candidateName, String jobTitle, String companyName, String scheduledAt, String location, String calendarLink) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#FF9800;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".button{display:inline-block;padding:12px 24px;background-color:#4CAF50;color:white;text-decoration:none;border-radius:5px;margin:10px 0;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - Interview Scheduled</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(candidateName).append(",</p>\n");
        template.append("<p>An interview has been scheduled for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong>.</p>\n");
        template.append("<p><strong>Date & Time:</strong> ").append(scheduledAt).append("</p>\n");
        if (location != null && !location.isEmpty()) {
            template.append("<p><strong>Location:</strong> ").append(location).append("</p>\n");
        }
        if (calendarLink != null && !calendarLink.isEmpty()) {
            template.append("<p><a href=\"").append(calendarLink).append("\" class=\"button\">Add to Calendar</a></p>\n");
        }
        template.append("<p>Please confirm your attendance by logging into your HireHub account.</p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getInterviewCancelledTemplate(String recipientName, String jobTitle, String companyName, String scheduledAt, String reason) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#f44336;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - Interview Cancelled</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(recipientName).append(",</p>\n");
        template.append("<p>The interview for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong> scheduled for <strong>").append(scheduledAt).append("</strong> has been cancelled.</p>\n");
        if (reason != null && !reason.isEmpty()) {
            template.append("<p><strong>Reason:</strong> ").append(reason).append("</p>\n");
        }
        template.append("<p>Please log in to your HireHub account for more information.</p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getInterviewRescheduledTemplate(String recipientName, String jobTitle, String companyName, String oldDateTime, String newDateTime, String reason) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#FF9800;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - Interview Rescheduled</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(recipientName).append(",</p>\n");
        template.append("<p>The interview for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong> has been rescheduled.</p>\n");
        template.append("<p><strong>Previous Date & Time:</strong> ").append(oldDateTime).append("</p>\n");
        template.append("<p><strong>New Date & Time:</strong> ").append(newDateTime).append("</p>\n");
        if (reason != null && !reason.isEmpty()) {
            template.append("<p><strong>Reason:</strong> ").append(reason).append("</p>\n");
        }
        template.append("<p>Please log in to your HireHub account to confirm the new schedule.</p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getNewMessageTemplate(String recipientName, String senderName, String messagePreview, String conversationLink) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#9C27B0;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".button{display:inline-block;padding:12px 24px;background-color:#9C27B0;color:white;text-decoration:none;border-radius:5px;margin:10px 0;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - New Message</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(recipientName).append(",</p>\n");
        template.append("<p>You have received a new message from <strong>").append(senderName).append("</strong>.</p>\n");
        template.append("<p><em>").append(messagePreview).append("</em></p>\n");
        if (conversationLink != null && !conversationLink.isEmpty()) {
            template.append("<p><a href=\"").append(conversationLink).append("\" class=\"button\">View Message</a></p>\n");
        }
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getWelcomeEmailTemplate(String userName, String role) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#4CAF50;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".button{display:inline-block;padding:12px 24px;background-color:#4CAF50;color:white;text-decoration:none;border-radius:5px;margin:10px 0;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>Welcome to HireHub!</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(userName).append(",</p>\n");
        template.append("<p>Welcome to HireHub! We're excited to have you join our platform as a <strong>").append(role).append("</strong>.</p>\n");
        template.append("<p>Get started by completing your profile and exploring opportunities.</p>\n");
        template.append("<p><a href=\"#\" class=\"button\">Get Started</a></p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }

    @Override
    public String getPasswordResetTemplate(String userName, String resetLink) {
        StringBuilder template = new StringBuilder();
        template.append("<!DOCTYPE html>\n");
        template.append("<html>\n");
        template.append("<head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}");
        template.append(".container{max-width:600px;margin:0 auto;padding:20px;}");
        template.append(".header{background-color:#2196F3;color:white;padding:20px;text-align:center;}");
        template.append(".content{padding:20px;background-color:#f9f9f9;}");
        template.append(".button{display:inline-block;padding:12px 24px;background-color:#2196F3;color:white;text-decoration:none;border-radius:5px;margin:10px 0;}");
        template.append(".footer{text-align:center;padding:20px;color:#666;font-size:12px;}</style></head>\n");
        template.append("<body>\n");
        template.append("<div class=\"container\">\n");
        template.append("<div class=\"header\"><h2>HireHub - Password Reset</h2></div>\n");
        template.append("<div class=\"content\">\n");
        template.append("<p>Dear ").append(userName).append(",</p>\n");
        template.append("<p>You requested to reset your password. Click the button below to reset it:</p>\n");
        template.append("<p><a href=\"").append(resetLink).append("\" class=\"button\">Reset Password</a></p>\n");
        template.append("<p>If you didn't request this, please ignore this email.</p>\n");
        template.append("<p>This link will expire in 24 hours.</p>\n");
        template.append("<p>Best regards,<br>The HireHub Team</p>\n");
        template.append("</div>\n");
        template.append("<div class=\"footer\"><p>&copy; 2024 HireHub. All rights reserved.</p></div>\n");
        template.append("</div>\n");
        template.append("</body>\n");
        template.append("</html>");
        return template.toString();
    }
}






