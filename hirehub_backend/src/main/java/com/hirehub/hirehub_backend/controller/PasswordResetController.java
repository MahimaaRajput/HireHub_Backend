package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.PasswordResetDto;
import com.hirehub.hirehub_backend.dto.PasswordResetRequestDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDto> requestPasswordReset(@RequestBody PasswordResetRequestDto request) {
        try {
            passwordResetService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("If an account exists with this email, a password reset link has been sent.")
                    .status("success")
                    .build());
        } catch (Exception e) {
            // Always return success message for security (don't reveal if email exists)
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("If an account exists with this email, a password reset link has been sent.")
                    .status("success")
                    .build());
        }
    }

    @GetMapping("/reset-password/verify")
    public ResponseEntity<ResponseDto> verifyResetToken(@RequestParam String token) {
        try {
            boolean isValid = passwordResetService.verifyResetToken(token);
            if (isValid) {
                return ResponseEntity.ok(ResponseDto.builder()
                        .message("Reset token is valid")
                        .status("success")
                        .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message("Invalid reset token")
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

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto> resetPassword(@RequestBody PasswordResetDto request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("Password reset successfully. You can now login with your new password.")
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



