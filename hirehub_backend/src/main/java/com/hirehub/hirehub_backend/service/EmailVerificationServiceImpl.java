package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int TOKEN_EXPIRY_HOURS = 24;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void sendVerificationEmail(User user) throws Exception {
        String token = generateVerificationToken();
        LocalDateTime expiry = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);

        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiry(expiry);
        userRepository.save(user);

        String verificationLink = baseUrl + "/api/common/verify-email?token=" + token;
        String subject = "Verify Your Email - HireHub";
        String htmlBody = buildVerificationEmailBody(user.getFullName(), verificationLink);

        emailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
    }

    @Override
    public boolean verifyEmail(String token) throws Exception {
        Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);
        
        if (userOpt.isEmpty()) {
            throw new Exception("Invalid verification token");
        }

        User user = userOpt.get();

        if (user.getEmailVerificationTokenExpiry() == null || 
            user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new Exception("Verification token has expired. Please request a new one.");
        }

        if (user.getEmailVerified()) {
            throw new Exception("Email is already verified");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        userRepository.save(user);

        return true;
    }

    @Override
    public void resendVerificationEmail(String email) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with email: " + email);
        }

        User user = userOpt.get();

        if (user.getEmailVerified()) {
            throw new Exception("Email is already verified");
        }

        sendVerificationEmail(user);
    }

    @Override
    public String generateVerificationToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String buildVerificationEmailBody(String userName, String verificationLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".button { display: inline-block; padding: 12px 30px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Verify Your Email Address</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hello " + userName + ",</p>" +
                "<p>Thank you for registering with HireHub! Please verify your email address by clicking the button below:</p>" +
                "<div style='text-align: center;'>" +
                "<a href='" + verificationLink + "' class='button'>Verify Email</a>" +
                "</div>" +
                "<p>Or copy and paste this link into your browser:</p>" +
                "<p style='word-break: break-all; color: #4CAF50;'>" + verificationLink + "</p>" +
                "<p>This link will expire in 24 hours.</p>" +
                "<p>If you didn't create an account with HireHub, please ignore this email.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2024 HireHub. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

