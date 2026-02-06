package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.JobSeekerAnalyticsDto;
import com.hirehub.hirehub_backend.service.JobSeekerAnalyticsService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class JobSeekerAnalyticsController {

    @Autowired
    private JobSeekerAnalyticsService jobSeekerAnalyticsService;

    @Autowired
    private UserService userService;

    // Get job seeker analytics
    @GetMapping("/analytics")
    public ResponseEntity<JobSeekerAnalyticsDto> getJobSeekerAnalytics(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long jobSeekerId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        JobSeekerAnalyticsDto analytics = jobSeekerAnalyticsService.getJobSeekerAnalytics(jobSeekerId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}



