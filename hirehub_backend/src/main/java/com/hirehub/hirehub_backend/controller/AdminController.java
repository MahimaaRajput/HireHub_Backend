package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.AdminDashboardDto;
import com.hirehub.hirehub_backend.service.AdminAnalyticsService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Autowired
    private UserService userService;

    // Get admin dashboard statistics
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getAdminDashboard(
            @RequestHeader("Authorization") String jwt) throws Exception {
        // Verify user is admin (would need to check role from JWT)
        // For now, we'll allow access - in production, add proper admin role check
        AdminDashboardDto dashboard = adminAnalyticsService.getAdminDashboard();
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }
}

