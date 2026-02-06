package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.dto.TwoFactorSetupDto;
import com.hirehub.hirehub_backend.dto.TwoFactorVerifyDto;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import com.hirehub.hirehub_backend.service.TwoFactorAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/2fa/setup")
    public ResponseEntity<TwoFactorSetupDto> setup2FA(
            @RequestHeader("Authorization") String jwt) {
        try {
            Long userId = JwtProvider.getUserIdFromToken(jwt);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            User user = userOpt.get();
            
            if (twoFactorAuthService.is2FAEnabled(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(TwoFactorSetupDto.builder()
                                .message("2FA is already enabled for this account")
                                .build());
            }

            String secretKey = twoFactorAuthService.generateSecretKey();
            String qrCodeUrl = twoFactorAuthService.generateQrCodeUrl(user, secretKey);

            return ResponseEntity.ok(TwoFactorSetupDto.builder()
                    .secretKey(secretKey)
                    .qrCodeUrl(qrCodeUrl)
                    .message("Scan the QR code with your authenticator app and enter the verification code to enable 2FA")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TwoFactorSetupDto.builder()
                            .message("Error setting up 2FA: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<ResponseDto> enable2FA(
            @RequestHeader("Authorization") String jwt,
            @RequestBody TwoFactorVerifyDto verifyDto) {
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
            
            if (verifyDto.getSecretKey() == null || verifyDto.getSecretKey().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Secret key is required. Please call /2fa/setup first.")
                                .status("error")
                                .build());
            }

            String secretKey = verifyDto.getSecretKey();
            
            // Verify the code with the provided secret
            if (!twoFactorAuthService.verifyTotpCode(secretKey, verifyDto.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Invalid verification code")
                                .status("error")
                                .build());
            }

            // Enable 2FA with the verified secret
            twoFactorAuthService.enable2FA(user, secretKey, verifyDto.getCode());
            
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("2FA enabled successfully")
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

    @PostMapping("/2fa/verify")
    public ResponseEntity<ResponseDto> verify2FA(
            @RequestHeader("Authorization") String jwt,
            @RequestBody TwoFactorVerifyDto verifyDto) {
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
            
            if (!twoFactorAuthService.is2FAEnabled(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("2FA is not enabled for this account")
                                .status("error")
                                .build());
            }

            if (user.getTwoFactorSecret() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("2FA secret not found")
                                .status("error")
                                .build());
            }

            boolean isValid = twoFactorAuthService.verifyTotpCode(user.getTwoFactorSecret(), verifyDto.getCode());
            
            if (isValid) {
                return ResponseEntity.ok(ResponseDto.builder()
                        .message("2FA code verified successfully")
                        .status("success")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Invalid 2FA code")
                                .status("error")
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message(e.getMessage())
                            .status("error")
                            .build());
        }
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<ResponseDto> disable2FA(
            @RequestHeader("Authorization") String jwt,
            @RequestBody TwoFactorVerifyDto verifyDto) {
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
            
            if (!twoFactorAuthService.is2FAEnabled(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("2FA is not enabled for this account")
                                .status("error")
                                .build());
            }

            // Verify code before disabling
            if (user.getTwoFactorSecret() != null) {
                boolean isValid = twoFactorAuthService.verifyTotpCode(user.getTwoFactorSecret(), verifyDto.getCode());
                if (!isValid) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseDto.builder()
                                    .message("Invalid 2FA code. Cannot disable 2FA.")
                                    .status("error")
                                    .build());
                }
            }

            twoFactorAuthService.disable2FA(user);
            
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("2FA disabled successfully")
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

    @GetMapping("/2fa/status")
    public ResponseEntity<ResponseDto> get2FAStatus(
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
            boolean isEnabled = twoFactorAuthService.is2FAEnabled(user);
            
            return ResponseEntity.ok(ResponseDto.builder()
                    .message(isEnabled ? "2FA is enabled" : "2FA is disabled")
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

