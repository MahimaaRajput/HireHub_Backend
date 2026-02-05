package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.InterviewDto;
import com.hirehub.hirehub_backend.service.InterviewService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private UserService userService;

    // Schedule interview (RECRUITER only)
    @PostMapping("/recruiter/interview/schedule")
    public ResponseEntity<InterviewDto> scheduleInterview(
            @RequestHeader("Authorization") String jwt,
            @RequestBody InterviewDto interviewDto) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.scheduleInterview(interviewDto, recruiterId);
        return new ResponseEntity<>(interview, HttpStatus.CREATED);
    }

    // Update interview (RECRUITER or USER)
    @PutMapping("/common/interview/{interviewId}")
    public ResponseEntity<InterviewDto> updateInterview(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId,
            @RequestBody InterviewDto interviewDto) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.updateInterview(interviewId, interviewDto, userId);
        return new ResponseEntity<>(interview, HttpStatus.OK);
    }

    // Cancel interview (RECRUITER or USER)
    @PutMapping("/common/interview/{interviewId}/cancel")
    public ResponseEntity<InterviewDto> cancelInterview(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId,
            @RequestParam(required = false) String reason) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.cancelInterview(interviewId, reason, userId);
        return new ResponseEntity<>(interview, HttpStatus.OK);
    }

    // Confirm interview (USER only)
    @PutMapping("/user/interview/{interviewId}/confirm")
    public ResponseEntity<InterviewDto> confirmInterview(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId) throws Exception {
        Long candidateId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.confirmInterview(interviewId, candidateId);
        return new ResponseEntity<>(interview, HttpStatus.OK);
    }

    // Reschedule interview (RECRUITER or USER)
    @PutMapping("/common/interview/{interviewId}/reschedule")
    public ResponseEntity<InterviewDto> rescheduleInterview(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId,
            @RequestParam String newDateTime,
            @RequestParam(required = false) String reason) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        LocalDateTime newDate = LocalDateTime.parse(newDateTime);
        InterviewDto interview = interviewService.rescheduleInterview(interviewId, newDate, reason, userId);
        return new ResponseEntity<>(interview, HttpStatus.OK);
    }

    // Add feedback (RECRUITER only)
    @PutMapping("/recruiter/interview/{interviewId}/feedback")
    public ResponseEntity<InterviewDto> addFeedback(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId,
            @RequestParam String feedback) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.addFeedback(interviewId, feedback, recruiterId);
        return new ResponseEntity<>(interview, HttpStatus.OK);
    }

    // Get interview by ID
    @GetMapping("/common/interview/{interviewId}")
    public ResponseEntity<InterviewDto> getInterviewById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.getInterviewById(interviewId, userId);
        return new ResponseEntity<>(interview, HttpStatus.OK);
    }

    // Get candidate interviews (USER)
    @GetMapping("/user/interviews")
    public ResponseEntity<List<InterviewDto>> getCandidateInterviews(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long candidateId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        List<InterviewDto> interviews = interviewService.getCandidateInterviews(candidateId);
        return new ResponseEntity<>(interviews, HttpStatus.OK);
    }

    // Get recruiter interviews (RECRUITER)
    @GetMapping("/recruiter/interviews")
    public ResponseEntity<List<InterviewDto>> getRecruiterInterviews(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        List<InterviewDto> interviews = interviewService.getRecruiterInterviews(recruiterId);
        return new ResponseEntity<>(interviews, HttpStatus.OK);
    }

    // Get upcoming interviews for candidate (USER)
    @GetMapping("/user/interviews/upcoming")
    public ResponseEntity<List<InterviewDto>> getUpcomingInterviewsForCandidate(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long candidateId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        List<InterviewDto> interviews = interviewService.getUpcomingInterviewsForCandidate(candidateId);
        return new ResponseEntity<>(interviews, HttpStatus.OK);
    }

    // Get upcoming interviews for recruiter (RECRUITER)
    @GetMapping("/recruiter/interviews/upcoming")
    public ResponseEntity<List<InterviewDto>> getUpcomingInterviewsForRecruiter(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        List<InterviewDto> interviews = interviewService.getUpcomingInterviewsForRecruiter(recruiterId);
        return new ResponseEntity<>(interviews, HttpStatus.OK);
    }

    // Get interviews by application (RECRUITER)
    @GetMapping("/recruiter/interviews/application/{applicantId}")
    public ResponseEntity<List<InterviewDto>> getInterviewsByApplication(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long applicantId) throws Exception {
        List<InterviewDto> interviews = interviewService.getInterviewsByApplication(applicantId);
        return new ResponseEntity<>(interviews, HttpStatus.OK);
    }

    // Generate calendar link
    @GetMapping("/common/interview/{interviewId}/calendar-link")
    public ResponseEntity<String> generateCalendarLink(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long interviewId) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        InterviewDto interview = interviewService.getInterviewById(interviewId, userId);
        String calendarLink = interviewService.generateCalendarLink(interview);
        return new ResponseEntity<>(calendarLink, HttpStatus.OK);
    }
}



