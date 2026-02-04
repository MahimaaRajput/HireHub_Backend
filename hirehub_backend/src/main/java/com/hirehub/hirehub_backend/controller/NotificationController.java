package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.NotificationDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/notifications/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        Long count = notificationService.getUnreadCount(userId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    
    @PostMapping("/notifications/{notificationId}/read")
    public ResponseEntity<NotificationDto> markAsRead(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long notificationId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        NotificationDto notification = notificationService.markAsRead(notificationId, userId);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }
    
    @PostMapping("/notifications/mark-all-read")
    public ResponseEntity<ResponseDto> markAllAsRead(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        notificationService.markAllAsRead(userId);
        return new ResponseEntity<>(new ResponseDto("All notifications marked as read"), HttpStatus.OK);
    }
}




