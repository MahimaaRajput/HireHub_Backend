package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import com.hirehub.hirehub_backend.service.PhoneVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class PhoneVerificationController {

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send-phone-verification-otp")
    public ResponseEntity<ResponseDto> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt) {
        try {
            Long userId = JwtProvider.getUserIdFromToken(jwt);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.builder()
                                .message("User not found")
                                .status("error")
                                .build());
            }

            User user = userOpt.get();
            if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Phone number not set. Please update your profile with a phone number first.")
                                .status("error")
                                .build());
            }

            phoneVerificationService.sendVerificationOtp(user);
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("OTP sent successfully to your phone number. Please check your SMS or in-app notifications.")
                    .status("success")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message(e.getMessage())
                            .status("error")
                            .build());
        }
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<ResponseDto> verifyPhone(
            @RequestHeader("Authorization") String jwt,
            @RequestParam String otp) {
        try {
            Long userId = JwtProvider.getUserIdFromToken(jwt);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.builder()
                                .message("User not found")
                                .status("error")
                                .build());
            }

            User user = userOpt.get();
            if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Phone number not set")
                                .status("error")
                                .build());
            }

            boolean verified = phoneVerificationService.verifyPhone(user.getPhoneNumber(), otp);
            if (verified) {
                return ResponseEntity.ok(ResponseDto.builder()
                        .message("Phone number verified successfully!")
                        .status("success")
                        .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message("Phone verification failed")
                            .status("error")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message(e.getMessage())
                            .status("error")
                            .build());
        }
    }

    @PostMapping("/resend-phone-verification-otp")
    public ResponseEntity<ResponseDto> resendVerificationOtp(
            @RequestHeader("Authorization") String jwt) {
        try {
            Long userId = JwtProvider.getUserIdFromToken(jwt);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.builder()
                                .message("User not found")
                                .status("error")
                                .build());
            }

            User user = userOpt.get();
            if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Phone number not set. Please update your profile with a phone number first.")
                                .status("error")
                                .build());
            }

            phoneVerificationService.resendVerificationOtp(user.getPhoneNumber());
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("OTP resent successfully. Please check your SMS or in-app notifications.")
                    .status("success")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message(e.getMessage())
                            .status("error")
                            .build());
        }
    }
}




