package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.NotificationDto;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneVerificationServiceImpl implements PhoneVerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void sendVerificationOtp(User user) throws Exception {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new Exception("Phone number is required for verification");
        }

        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        user.setPhoneVerificationOtp(otp);
        user.setPhoneVerificationOtpExpiry(expiry);
        userRepository.save(user);

        // Send OTP via SMS (simulated for now - in production, integrate with SMS service like Twilio, AWS SNS, etc.)
        sendOtpSms(user.getPhoneNumber(), otp);

        // Also send in-app notification
        try {
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setUserId(user.getId());
            notificationDto.setTitle("Phone Verification OTP");
            notificationDto.setMessage("Your verification OTP is: " + otp + ". This OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");
            notificationDto.setType("VERIFICATION");
            notificationDto.setRelatedEntityType("PHONE_VERIFICATION");
            notificationService.createNotification(user.getId(), notificationDto);
        } catch (Exception e) {
            // Log but don't fail if notification fails
            System.err.println("Failed to create notification: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPhone(String phoneNumber, String otp) throws Exception {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with phone number: " + phoneNumber);
        }

        User user = userOpt.get();

        if (user.getPhoneVerificationOtp() == null || user.getPhoneVerificationOtp().isEmpty()) {
            throw new Exception("No OTP found. Please request a new OTP.");
        }

        if (user.getPhoneVerificationOtpExpiry() == null || 
            user.getPhoneVerificationOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new Exception("OTP has expired. Please request a new one.");
        }

        if (!user.getPhoneVerificationOtp().equals(otp)) {
            throw new Exception("Invalid OTP. Please try again.");
        }

        if (user.getPhoneVerified()) {
            throw new Exception("Phone number is already verified");
        }

        user.setPhoneVerified(true);
        user.setPhoneVerificationOtp(null);
        user.setPhoneVerificationOtpExpiry(null);
        userRepository.save(user);

        return true;
    }

    @Override
    public void resendVerificationOtp(String phoneNumber) throws Exception {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with phone number: " + phoneNumber);
        }

        User user = userOpt.get();

        if (user.getPhoneVerified()) {
            throw new Exception("Phone number is already verified");
        }

        sendVerificationOtp(user);
    }

    @Override
    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void sendOtpSms(String phoneNumber, String otp) {
        // In production, integrate with SMS service provider (Twilio, AWS SNS, etc.)
        // For now, just log the OTP (in development/testing)
        System.out.println("SMS OTP for " + phoneNumber + ": " + otp);
        System.out.println("In production, this would be sent via SMS service.");
        
        // Example integration (commented out - uncomment and configure when ready):
        /*
        try {
            // Twilio example:
            // Message message = Message.creator(
            //     new PhoneNumber(phoneNumber),
            //     new PhoneNumber("YOUR_TWILIO_NUMBER"),
            //     "Your HireHub verification code is: " + otp
            // ).create();
            
            // AWS SNS example:
            // PublishRequest request = new PublishRequest()
            //     .withMessage("Your HireHub verification code is: " + otp)
            //     .withPhoneNumber(phoneNumber);
            // snsClient.publish(request);
        } catch (Exception e) {
            throw new Exception("Failed to send SMS: " + e.getMessage());
        }
        */
    }
}

