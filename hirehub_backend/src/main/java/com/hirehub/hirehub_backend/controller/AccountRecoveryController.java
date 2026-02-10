package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.AccountRecoveryRequestDto;
import com.hirehub.hirehub_backend.dto.AccountRecoveryVerifyDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.service.AccountRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class AccountRecoveryController {

    @Autowired
    private AccountRecoveryService accountRecoveryService;

    @PostMapping("/account-recovery/initiate")
    public ResponseEntity<ResponseDto> initiateRecovery(@RequestBody AccountRecoveryRequestDto request) {
        try {
            if (request.getType() == null || request.getIdentifier() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Type and identifier are required")
                                .status("error")
                                .build());
            }

            if ("email".equalsIgnoreCase(request.getType())) {
                accountRecoveryService.initiateRecoveryByEmail(request.getIdentifier());
            } else if ("phone".equalsIgnoreCase(request.getType())) {
                accountRecoveryService.initiateRecoveryByPhone(request.getIdentifier());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.builder()
                                .message("Invalid type. Use 'email' or 'phone'")
                                .status("error")
                                .build());
            }

            return ResponseEntity.ok(ResponseDto.builder()
                    .message("If an account exists, a recovery code has been sent to your " + request.getType() + ".")
                    .status("success")
                    .build());
        } catch (Exception e) {
            // Always return success message for security
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("If an account exists, a recovery code has been sent.")
                    .status("success")
                    .build());
        }
    }

    @PostMapping("/account-recovery/verify")
    public ResponseEntity<ResponseDto> verifyRecoveryCode(@RequestBody AccountRecoveryVerifyDto request) {
        try {
            boolean isValid = accountRecoveryService.verifyRecoveryCode(
                    request.getIdentifier(), 
                    request.getCode(), 
                    request.getType()
            );
            
            if (isValid) {
                return ResponseEntity.ok(ResponseDto.builder()
                        .message("Recovery code verified successfully")
                        .status("success")
                        .build());
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .message("Invalid recovery code")
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

    @PostMapping("/account-recovery/recover")
    public ResponseEntity<ResponseDto> recoverAccount(@RequestBody AccountRecoveryVerifyDto request) {
        try {
            User user = accountRecoveryService.recoverAccount(
                    request.getIdentifier(), 
                    request.getCode(), 
                    request.getType()
            );
            
            return ResponseEntity.ok(ResponseDto.builder()
                    .message("Account recovered successfully. User ID: " + user.getId() + ". You can now login or reset your password.")
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



