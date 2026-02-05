package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.dto.UserNotificationPreferencesDto;
import com.hirehub.hirehub_backend.service.UserNotificationPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
public class NotificationPreferencesController {
    
    @Autowired
    private UserNotificationPreferencesService preferencesService;
    
    @GetMapping("/notification-preferences")
    public ResponseEntity<UserNotificationPreferencesDto> getPreferences(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        UserNotificationPreferencesDto preferences = preferencesService.getPreferences(userId);
        return new ResponseEntity<>(preferences, HttpStatus.OK);
    }
    
    @PutMapping("/notification-preferences")
    public ResponseEntity<UserNotificationPreferencesDto> updatePreferences(
            @RequestHeader("Authorization") String jwt,
            @RequestBody UserNotificationPreferencesDto preferencesDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        UserNotificationPreferencesDto updatedPreferences = preferencesService.updatePreferences(userId, preferencesDto);
        return new ResponseEntity<>(updatedPreferences, HttpStatus.OK);
    }
}





