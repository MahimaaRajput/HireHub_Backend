package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.RecruiterAnalyticsDto;
import com.hirehub.hirehub_backend.service.RecruiterAnalyticsService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterAnalyticsController {

    @Autowired
    private RecruiterAnalyticsService recruiterAnalyticsService;

    @Autowired
    private UserService userService;

    // Get overall recruiter analytics
    @GetMapping("/analytics")
    public ResponseEntity<RecruiterAnalyticsDto> getRecruiterAnalytics(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        RecruiterAnalyticsDto analytics = recruiterAnalyticsService.getRecruiterAnalytics(recruiterId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    // Get analytics for a specific job
    @GetMapping("/analytics/job/{jobId}")
    public ResponseEntity<RecruiterAnalyticsDto> getJobAnalytics(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long jobId) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        RecruiterAnalyticsDto analytics = recruiterAnalyticsService.getJobAnalytics(jobId, recruiterId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}


