package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.JobAlertDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.JobAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class JobAlertController {
    
    @Autowired
    private JobAlertService jobAlertService;
    
    @PostMapping("/job-alert")
    public ResponseEntity<JobAlertDto> createJobAlert(
            @RequestHeader("Authorization") String jwt,
            @RequestBody JobAlertDto alertDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        JobAlertDto createdAlert = jobAlertService.createJobAlert(userId, alertDto);
        return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
    }
    
    @PutMapping("/job-alert/{alertId}")
    public ResponseEntity<JobAlertDto> updateJobAlert(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long alertId,
            @RequestBody JobAlertDto alertDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        JobAlertDto updatedAlert = jobAlertService.updateJobAlert(userId, alertId, alertDto);
        return new ResponseEntity<>(updatedAlert, HttpStatus.OK);
    }
    
    @DeleteMapping("/job-alert/{alertId}")
    public ResponseEntity<ResponseDto> deleteJobAlert(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long alertId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        jobAlertService.deleteJobAlert(userId, alertId);
        return new ResponseEntity<>(new ResponseDto("Job alert deleted successfully"), HttpStatus.OK);
    }
    
    @GetMapping("/job-alerts")
    public ResponseEntity<List<JobAlertDto>> getUserJobAlerts(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<JobAlertDto> alerts = jobAlertService.getUserJobAlerts(userId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }
    
    @GetMapping("/job-alert/{alertId}")
    public ResponseEntity<JobAlertDto> getJobAlertById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long alertId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        JobAlertDto alert = jobAlertService.getJobAlertById(userId, alertId);
        return new ResponseEntity<>(alert, HttpStatus.OK);
    }
    
    @PostMapping("/job-alert/{alertId}/toggle")
    public ResponseEntity<ResponseDto> toggleJobAlert(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long alertId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        jobAlertService.toggleJobAlert(userId, alertId);
        return new ResponseEntity<>(new ResponseDto("Job alert toggled successfully"), HttpStatus.OK);
    }
    
    @GetMapping("/job-alert/{alertId}/matching-jobs")
    public ResponseEntity<List<JobDto>> getMatchingJobsForAlert(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long alertId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        // Verify user owns the alert
        jobAlertService.getJobAlertById(userId, alertId);
        List<JobDto> matchingJobs = jobAlertService.getMatchingJobsForAlert(alertId);
        return new ResponseEntity<>(matchingJobs, HttpStatus.OK);
    }
}
