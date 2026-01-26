package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ResumeDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class ResumeController {
    
    @Autowired
    private ResumeService resumeService;
    
    @PostMapping("/resume")
    public ResponseEntity<ResumeDto> createResume(
            @RequestHeader("Authorization") String jwt,
            @RequestBody ResumeDto resumeDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        ResumeDto createdResume = resumeService.createResume(userId, resumeDto);
        return new ResponseEntity<>(createdResume, HttpStatus.CREATED);
    }
    
    @PutMapping("/resume/{resumeId}")
    public ResponseEntity<ResumeDto> updateResume(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long resumeId,
            @RequestBody ResumeDto resumeDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        ResumeDto updatedResume = resumeService.updateResume(userId, resumeId, resumeDto);
        return new ResponseEntity<>(updatedResume, HttpStatus.OK);
    }
    
    @DeleteMapping("/resume/{resumeId}")
    public ResponseEntity<ResponseDto> deleteResume(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long resumeId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        resumeService.deleteResume(userId, resumeId);
        return new ResponseEntity<>(new ResponseDto("Resume deleted successfully"), HttpStatus.OK);
    }
    
    @GetMapping("/resumes")
    public ResponseEntity<List<ResumeDto>> getUserResumes(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<ResumeDto> resumes = resumeService.getUserResumes(userId);
        return new ResponseEntity<>(resumes, HttpStatus.OK);
    }
    
    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<ResumeDto> getResumeById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long resumeId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        ResumeDto resume = resumeService.getResumeById(userId, resumeId);
        return new ResponseEntity<>(resume, HttpStatus.OK);
    }
    
    @PostMapping("/resume/{resumeId}/set-default")
    public ResponseEntity<ResumeDto> setDefaultResume(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long resumeId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        ResumeDto resume = resumeService.setDefaultResume(userId, resumeId);
        return new ResponseEntity<>(resume, HttpStatus.OK);
    }
    
    @GetMapping("/resume/default")
    public ResponseEntity<ResumeDto> getDefaultResume(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        ResumeDto resume = resumeService.getDefaultResume(userId);
        return new ResponseEntity<>(resume, HttpStatus.OK);
    }
}

