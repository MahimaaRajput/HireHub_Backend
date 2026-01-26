package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.SavedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class SavedJobController {
    
    @Autowired
    private SavedJobService savedJobService;
    
    @PostMapping("/save-job/{jobId}")
    public ResponseEntity<ResponseDto> saveJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId is null");
        }
        savedJobService.saveJob(userId, jobId);
        return new ResponseEntity<>(new ResponseDto("Job saved successfully"), HttpStatus.OK);
    }
    
    @DeleteMapping("/unsave-job/{jobId}")
    public ResponseEntity<ResponseDto> unsaveJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId is null");
        }
        savedJobService.unsaveJob(userId, jobId);
        return new ResponseEntity<>(new ResponseDto("Job removed from saved list"), HttpStatus.OK);
    }
    
    @GetMapping("/saved-jobs")
    public ResponseEntity<List<JobDto>> getSavedJobs(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId is null");
        }
        List<JobDto> savedJobs = savedJobService.getSavedJobs(userId);
        return new ResponseEntity<>(savedJobs, HttpStatus.OK);
    }
    
    @GetMapping("/is-saved/{jobId}")
    public ResponseEntity<Boolean> isJobSaved(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId is null");
        }
        boolean isSaved = savedJobService.isJobSaved(userId, jobId);
        return new ResponseEntity<>(isSaved, HttpStatus.OK);
    }
}

