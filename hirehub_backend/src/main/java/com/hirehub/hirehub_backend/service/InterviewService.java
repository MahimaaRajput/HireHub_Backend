package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.InterviewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewService {
    // Schedule a new interview
    InterviewDto scheduleInterview(InterviewDto interviewDto, Long recruiterId) throws Exception;

    // Update interview details
    InterviewDto updateInterview(Long interviewId, InterviewDto interviewDto, Long userId) throws Exception;

    // Cancel an interview
    InterviewDto cancelInterview(Long interviewId, String reason, Long userId) throws Exception;

    // Confirm interview (by candidate)
    InterviewDto confirmInterview(Long interviewId, Long candidateId) throws Exception;

    // Reschedule interview
    InterviewDto rescheduleInterview(Long interviewId, LocalDateTime newDateTime, String reason, Long userId) throws Exception;

    // Add interview feedback
    InterviewDto addFeedback(Long interviewId, String feedback, Long recruiterId) throws Exception;

    // Get interview by ID
    InterviewDto getInterviewById(Long interviewId, Long userId) throws Exception;

    // Get all interviews for a candidate
    List<InterviewDto> getCandidateInterviews(Long candidateId) throws Exception;

    // Get all interviews for a recruiter
    List<InterviewDto> getRecruiterInterviews(Long recruiterId) throws Exception;

    // Get upcoming interviews for a candidate
    List<InterviewDto> getUpcomingInterviewsForCandidate(Long candidateId) throws Exception;

    // Get upcoming interviews for a recruiter
    List<InterviewDto> getUpcomingInterviewsForRecruiter(Long recruiterId) throws Exception;

    // Get interviews for an application
    List<InterviewDto> getInterviewsByApplication(Long applicantId) throws Exception;

    // Generate calendar link (Google Calendar, Outlook, etc.)
    String generateCalendarLink(InterviewDto interviewDto) throws Exception;
}






