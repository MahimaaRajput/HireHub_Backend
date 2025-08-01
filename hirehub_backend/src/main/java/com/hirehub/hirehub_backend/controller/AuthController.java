package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.AuthResponse;
import com.hirehub.hirehub_backend.dto.UserRegisterRequest;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.service.AuthService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

public class AuthController {
    @Autowired
    AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegisterRequest user) throws Exception {
        AuthResponse response= authService.register(user);
        return ResponseEntity.ok(response);
    }
}
