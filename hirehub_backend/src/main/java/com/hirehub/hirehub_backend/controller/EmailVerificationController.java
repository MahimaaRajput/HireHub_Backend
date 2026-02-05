package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDto> verifyEmail(@RequestParam String token) {
        try {
            boolean verified = emailVerificationService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok(ResponseDto.builder()
                        .message("Email verified successfully!")
                        .status("success")
                        .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message("Email verification failed")
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

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ResponseDto> resendVerificationEmail(@RequestParam String email) {
        try {
            emailVerificationService.resendVerificationEmail(email);
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("Verification email sent successfully. Please check your inbox.")
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

