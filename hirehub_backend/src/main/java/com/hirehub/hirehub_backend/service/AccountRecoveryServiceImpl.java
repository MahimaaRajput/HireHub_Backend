package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountRecoveryServiceImpl implements AccountRecoveryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int RECOVERY_CODE_EXPIRY_MINUTES = 15;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void initiateRecoveryByEmail(String email) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists for security
            throw new Exception("If an account exists with this email, a recovery code has been sent.");
        }

        User user = userOpt.get();
        String recoveryCode = generateRecoveryCode();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(RECOVERY_CODE_EXPIRY_MINUTES);

        // Store recovery code temporarily (in production, use cache/Redis)
        // For now, we'll use emailVerificationToken field temporarily
        user.setEmailVerificationToken(recoveryCode);
        user.setEmailVerificationTokenExpiry(expiry);
        userRepository.save(user);

        String subject = "Account Recovery Code - HireHub";
        String htmlBody = buildRecoveryEmailBody(user.getFullName(), recoveryCode);

        emailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
    }

    @Override
    public void initiateRecoveryByPhone(String phoneNumber) throws Exception {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (userOpt.isEmpty()) {
            throw new Exception("If an account exists with this phone number, a recovery code has been sent.");
        }

        User user = userOpt.get();
        String recoveryCode = generateRecoveryCode();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(RECOVERY_CODE_EXPIRY_MINUTES);

        // Store recovery code temporarily in phoneVerificationOtp field
        user.setPhoneVerificationOtp(recoveryCode);
        user.setPhoneVerificationOtpExpiry(expiry);
        userRepository.save(user);

        // Send OTP via SMS (simulated)
        sendRecoverySms(phoneNumber, recoveryCode);

        // Also send in-app notification
        try {
            com.hirehub.hirehub_backend.dto.NotificationDto notificationDto = new com.hirehub.hirehub_backend.dto.NotificationDto();
            notificationDto.setUserId(user.getId());
            notificationDto.setTitle("Account Recovery Code");
            notificationDto.setMessage("Your account recovery code is: " + recoveryCode + ". This code will expire in " + RECOVERY_CODE_EXPIRY_MINUTES + " minutes.");
            notificationDto.setType("RECOVERY");
            notificationDto.setRelatedEntityType("ACCOUNT_RECOVERY");
            // Assuming NotificationService is available
            // notificationService.createNotification(user.getId(), notificationDto);
        } catch (Exception e) {
            System.err.println("Failed to create notification: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyRecoveryCode(String identifier, String code, String type) throws Exception {
        Optional<User> userOpt;
        
        if ("email".equalsIgnoreCase(type)) {
            userOpt = userRepository.findByEmail(identifier);
            if (userOpt.isEmpty()) {
                throw new Exception("User not found");
            }
            User user = userOpt.get();
            if (user.getEmailVerificationToken() == null || 
                !user.getEmailVerificationToken().equals(code)) {
                throw new Exception("Invalid recovery code");
            }
            if (user.getEmailVerificationTokenExpiry() == null || 
                user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new Exception("Recovery code has expired");
            }
        } else if ("phone".equalsIgnoreCase(type)) {
            userOpt = userRepository.findByPhoneNumber(identifier);
            if (userOpt.isEmpty()) {
                throw new Exception("User not found");
            }
            User user = userOpt.get();
            if (user.getPhoneVerificationOtp() == null || 
                !user.getPhoneVerificationOtp().equals(code)) {
                throw new Exception("Invalid recovery code");
            }
            if (user.getPhoneVerificationOtpExpiry() == null || 
                user.getPhoneVerificationOtpExpiry().isBefore(LocalDateTime.now())) {
                throw new Exception("Recovery code has expired");
            }
        } else {
            throw new Exception("Invalid recovery type. Use 'email' or 'phone'");
        }

        return true;
    }

    @Override
    public User recoverAccount(String identifier, String code, String type) throws Exception {
        // Verify the code first
        verifyRecoveryCode(identifier, code, type);

        Optional<User> userOpt;
        if ("email".equalsIgnoreCase(type)) {
            userOpt = userRepository.findByEmail(identifier);
        } else {
            userOpt = userRepository.findByPhoneNumber(identifier);
        }

        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        // Clear recovery codes
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        user.setPhoneVerificationOtp(null);
        user.setPhoneVerificationOtpExpiry(null);
        userRepository.save(user);

        return user;
    }

    @Override
    public String generateRecoveryCode() {
        // Generate 6-digit recovery code
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    private String buildRecoveryEmailBody(String userName, String recoveryCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".code { font-size: 32px; font-weight: bold; color: #4CAF50; text-align: center; padding: 20px; background-color: white; border: 2px dashed #4CAF50; margin: 20px 0; }" +
                ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
                ".warning { color: #d32f2f; font-weight: bold; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Account Recovery Code</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hello " + userName + ",</p>" +
                "<p>We received a request to recover your account. Use the code below to verify your identity:</p>" +
                "<div class='code'>" + recoveryCode + "</div>" +
                "<p class='warning'>This code will expire in " + RECOVERY_CODE_EXPIRY_MINUTES + " minutes.</p>" +
                "<p>If you didn't request account recovery, please ignore this email or contact support if you have concerns.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2024 HireHub. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private void sendRecoverySms(String phoneNumber, String code) {
        // In production, integrate with SMS service
        System.out.println("SMS Recovery Code for " + phoneNumber + ": " + code);
        System.out.println("In production, this would be sent via SMS service.");
    }
}

