package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.JobRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common")
public class JobRecommendationController {
    
    @Autowired
    private JobRecommendationService recommendationService;
    
    @PostMapping("/job/{jobId}/view")
    public ResponseEntity<ResponseDto> recordJobView(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long jobId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        recommendationService.recordJobView(userId, jobId);
        return new ResponseEntity<>(new ResponseDto("Job view recorded"), HttpStatus.OK);
    }
    
    @GetMapping("/jobs/recently-viewed")
    public ResponseEntity<List<JobDto>> getRecentlyViewedJobs(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false, defaultValue = "10") Integer limit) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<JobDto> jobs = recommendationService.getRecentlyViewedJobs(userId, limit);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }
    
    @GetMapping("/jobs/popular")
    public ResponseEntity<List<JobDto>> getPopularJobsInArea(
            @RequestParam String location,
            @RequestParam(required = false, defaultValue = "20") Integer limit) throws Exception {
        List<JobDto> jobs = recommendationService.getPopularJobsInArea(location, limit);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }
    
    @GetMapping("/jobs/{jobId}/similar")
    public ResponseEntity<List<JobDto>> getSimilarJobs(
            @PathVariable Long jobId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) throws Exception {
        List<JobDto> jobs = recommendationService.getSimilarJobs(jobId, limit);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }
    
    @GetMapping("/jobs/recommendations")
    public ResponseEntity<List<JobDto>> getJobRecommendations(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false, defaultValue = "20") Integer limit) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<JobDto> jobs = recommendationService.getJobRecommendationsBasedOnProfile(userId, limit);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }
}

