package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.AuthResponse;
import com.hirehub.hirehub_backend.dto.TwoFactorLoginDto;
import com.hirehub.hirehub_backend.dto.UserLoginRequest;
import com.hirehub.hirehub_backend.dto.UserRegisterRequest;
import com.hirehub.hirehub_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated

public class AuthController {
    @Autowired
    AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser( @Valid @RequestBody UserRegisterRequest user) throws Exception {
        AuthResponse response= authService.register(user);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message("failed")
                            .token(null)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/login/verify-2fa")
    public ResponseEntity<AuthResponse> verify2FALogin(@RequestBody TwoFactorLoginDto request) {
        try {
            AuthResponse response = authService.verify2FALogin(request.getEmail(), request.getCode());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message(e.getMessage())
                            .token(null)
                            .requires2FA(false)
                            .build());
        }
    }
}
