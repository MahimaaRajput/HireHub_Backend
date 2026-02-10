package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ApplicationDashboardDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class ApplicationController {
    
    @Autowired
    private JobService jobService;
    
    @GetMapping("/application-dashboard")
    public ResponseEntity<ApplicationDashboardDto> getApplicationDashboard(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        ApplicationDashboardDto dashboard = jobService.getApplicationDashboard(userId);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }
    
    @PostMapping("/withdraw-application/{applicationId}")
    public ResponseEntity<ResponseDto> withdrawApplication(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long applicationId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        jobService.withdrawApplication(userId, applicationId);
        return new ResponseEntity<>(new ResponseDto("Application withdrawn successfully"), HttpStatus.OK);
    }
}









