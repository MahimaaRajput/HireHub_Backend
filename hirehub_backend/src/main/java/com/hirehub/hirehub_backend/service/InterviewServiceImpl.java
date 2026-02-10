package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.InterviewDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Interview;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.ApplicantRepository;
import com.hirehub.hirehub_backend.repository.InterviewRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl implements InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public InterviewDto scheduleInterview(InterviewDto interviewDto, Long recruiterId) throws Exception {
        // Validate recruiter
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new Exception("Recruiter not found"));

        // Validate application
        Applicant application = applicantRepository.findById(interviewDto.getApplicationId())
                .orElseThrow(() -> new Exception("Application not found"));

        // Validate candidate
        User candidate = userRepository.findById(interviewDto.getCandidateId())
                .orElseThrow(() -> new Exception("Candidate not found"));

        // Verify recruiter has permission (should be the job poster)
        if (application.getJob().getPostedBy() == null || !application.getJob().getPostedBy().equals(recruiterId.toString())) {
            throw new Exception("You don't have permission to schedule interviews for this job");
        }

        // Create interview
        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setScheduledBy(recruiter);
        interview.setCandidate(candidate);
        interview.setScheduledAt(interviewDto.getScheduledAt());
        interview.setLocation(interviewDto.getLocation());
        interview.setInterviewType(interviewDto.getInterviewType());
        interview.setStatus("SCHEDULED");
        interview.setNotes(interviewDto.getNotes());

        // Generate calendar link
        String calendarLink = calendarService.generateGoogleCalendarLink(interviewDto);
        interview.setCalendarLink(calendarLink);

        interview = interviewRepository.save(interview);

        // Send notification to candidate
        try {
            String notificationTitle = "Interview Scheduled: " + application.getJob().getJobTitle();
            String notificationMessage = "An interview has been scheduled for " + interviewDto.getScheduledAt() + 
                (interviewDto.getLocation() != null ? " at " + interviewDto.getLocation() : "");
            com.hirehub.hirehub_backend.dto.NotificationDto notificationDto = new com.hirehub.hirehub_backend.dto.NotificationDto();
            notificationDto.setUserId(candidate.getId());
            notificationDto.setTitle(notificationTitle);
            notificationDto.setMessage(notificationMessage);
            notificationDto.setType("INTERVIEW");
            notificationDto.setRelatedEntityType("INTERVIEW");
            notificationDto.setRelatedEntityId(interview.getId());
            notificationService.createNotification(candidate.getId(), notificationDto);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToDto(interview);
    }

    @Override
    public InterviewDto updateInterview(Long interviewId, InterviewDto interviewDto, Long userId) throws Exception {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new Exception("Interview not found"));

        // Verify user has permission (recruiter or candidate)
        if (!interview.getScheduledBy().getId().equals(userId) && !interview.getCandidate().getId().equals(userId)) {
            throw new Exception("You don't have permission to update this interview");
        }

        // Update fields
        if (interviewDto.getScheduledAt() != null) {
            interview.setScheduledAt(interviewDto.getScheduledAt());
        }
        if (interviewDto.getLocation() != null) {
            interview.setLocation(interviewDto.getLocation());
        }
        if (interviewDto.getInterviewType() != null) {
            interview.setInterviewType(interviewDto.getInterviewType());
        }
        if (interviewDto.getNotes() != null) {
            interview.setNotes(interviewDto.getNotes());
        }

        // Regenerate calendar link if time changed
        if (interviewDto.getScheduledAt() != null) {
            InterviewDto currentDto = convertToDto(interview);
            currentDto.setScheduledAt(interviewDto.getScheduledAt());
            String calendarLink = calendarService.generateGoogleCalendarLink(currentDto);
            interview.setCalendarLink(calendarLink);
        }

        interview = interviewRepository.save(interview);
        return convertToDto(interview);
    }

    @Override
    public InterviewDto cancelInterview(Long interviewId, String reason, Long userId) throws Exception {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new Exception("Interview not found"));

        // Verify user has permission
        if (!interview.getScheduledBy().getId().equals(userId) && !interview.getCandidate().getId().equals(userId)) {
            throw new Exception("You don't have permission to cancel this interview");
        }

        interview.setStatus("CANCELLED");
        if (reason != null && !reason.isEmpty()) {
            interview.setNotes((interview.getNotes() != null ? interview.getNotes() + "\n\n" : "") + "Cancelled: " + reason);
        }

        interview = interviewRepository.save(interview);

        // Send notification to the other party
        Long notifyUserId = interview.getScheduledBy().getId().equals(userId) ? 
            interview.getCandidate().getId() : interview.getScheduledBy().getId();
        
        try {
            com.hirehub.hirehub_backend.dto.NotificationDto notificationDto = new com.hirehub.hirehub_backend.dto.NotificationDto();
            notificationDto.setUserId(notifyUserId);
            notificationDto.setTitle("Interview Cancelled: " + interview.getApplication().getJob().getJobTitle());
            notificationDto.setMessage("The interview scheduled for " + interview.getScheduledAt() + " has been cancelled." + 
                (reason != null ? " Reason: " + reason : ""));
            notificationDto.setType("INTERVIEW");
            notificationDto.setRelatedEntityType("INTERVIEW");
            notificationDto.setRelatedEntityId(interview.getId());
            notificationService.createNotification(notifyUserId, notificationDto);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToDto(interview);
    }

    @Override
    public InterviewDto confirmInterview(Long interviewId, Long candidateId) throws Exception {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new Exception("Interview not found"));

        if (!interview.getCandidate().getId().equals(candidateId)) {
            throw new Exception("You can only confirm your own interviews");
        }

        interview.setStatus("CONFIRMED");
        interview = interviewRepository.save(interview);

        // Send notification to recruiter
        try {
            com.hirehub.hirehub_backend.dto.NotificationDto notificationDto = new com.hirehub.hirehub_backend.dto.NotificationDto();
            notificationDto.setUserId(interview.getScheduledBy().getId());
            notificationDto.setTitle("Interview Confirmed: " + interview.getApplication().getJob().getJobTitle());
            notificationDto.setMessage(interview.getCandidate().getFullName() + " has confirmed the interview scheduled for " + interview.getScheduledAt());
            notificationDto.setType("INTERVIEW");
            notificationDto.setRelatedEntityType("INTERVIEW");
            notificationDto.setRelatedEntityId(interview.getId());
            notificationService.createNotification(interview.getScheduledBy().getId(), notificationDto);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToDto(interview);
    }

    @Override
    public InterviewDto rescheduleInterview(Long interviewId, LocalDateTime newDateTime, String reason, Long userId) throws Exception {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new Exception("Interview not found"));

        // Verify user has permission
        if (!interview.getScheduledBy().getId().equals(userId) && !interview.getCandidate().getId().equals(userId)) {
            throw new Exception("You don't have permission to reschedule this interview");
        }

        interview.setScheduledAt(newDateTime);
        interview.setStatus("RESCHEDULED");
        if (reason != null && !reason.isEmpty()) {
            interview.setNotes((interview.getNotes() != null ? interview.getNotes() + "\n\n" : "") + "Rescheduled: " + reason);
        }

        // Regenerate calendar link
        InterviewDto dto = convertToDto(interview);
        dto.setScheduledAt(newDateTime);
        String calendarLink = calendarService.generateGoogleCalendarLink(dto);
        interview.setCalendarLink(calendarLink);

        interview = interviewRepository.save(interview);

        // Send notification to the other party
        Long notifyUserId = interview.getScheduledBy().getId().equals(userId) ? 
            interview.getCandidate().getId() : interview.getScheduledBy().getId();
        
        try {
            com.hirehub.hirehub_backend.dto.NotificationDto notificationDto = new com.hirehub.hirehub_backend.dto.NotificationDto();
            notificationDto.setUserId(notifyUserId);
            notificationDto.setTitle("Interview Rescheduled: " + interview.getApplication().getJob().getJobTitle());
            notificationDto.setMessage("The interview has been rescheduled to " + newDateTime + 
                (reason != null ? ". Reason: " + reason : ""));
            notificationDto.setType("INTERVIEW");
            notificationDto.setRelatedEntityType("INTERVIEW");
            notificationDto.setRelatedEntityId(interview.getId());
            notificationService.createNotification(notifyUserId, notificationDto);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToDto(interview);
    }

    @Override
    public InterviewDto addFeedback(Long interviewId, String feedback, Long recruiterId) throws Exception {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new Exception("Interview not found"));

        if (!interview.getScheduledBy().getId().equals(recruiterId)) {
            throw new Exception("Only the recruiter who scheduled the interview can add feedback");
        }

        interview.setFeedback(feedback);
        interview.setStatus("COMPLETED");
        interview = interviewRepository.save(interview);

        return convertToDto(interview);
    }

    @Override
    public InterviewDto getInterviewById(Long interviewId, Long userId) throws Exception {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new Exception("Interview not found"));

        // Verify user has permission
        if (!interview.getScheduledBy().getId().equals(userId) && !interview.getCandidate().getId().equals(userId)) {
            throw new Exception("You don't have permission to view this interview");
        }

        return convertToDto(interview);
    }

    @Override
    public List<InterviewDto> getCandidateInterviews(Long candidateId) throws Exception {
        List<Interview> interviews = interviewRepository.findByCandidateId(candidateId);
        return interviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDto> getRecruiterInterviews(Long recruiterId) throws Exception {
        List<Interview> interviews = interviewRepository.findByRecruiterId(recruiterId);
        return interviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDto> getUpcomingInterviewsForCandidate(Long candidateId) throws Exception {
        List<Interview> interviews = interviewRepository.findUpcomingInterviewsForCandidate(candidateId, LocalDateTime.now());
        return interviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDto> getUpcomingInterviewsForRecruiter(Long recruiterId) throws Exception {
        List<Interview> interviews = interviewRepository.findUpcomingInterviewsForRecruiter(recruiterId, LocalDateTime.now());
        return interviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDto> getInterviewsByApplication(Long applicantId) throws Exception {
        List<Interview> interviews = interviewRepository.findByApplicationApplicantIdOrderByScheduledAtDesc(applicantId);
        return interviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String generateCalendarLink(InterviewDto interviewDto) throws Exception {
        return calendarService.generateGoogleCalendarLink(interviewDto);
    }

    private InterviewDto convertToDto(Interview interview) {
        InterviewDto dto = new InterviewDto();
        dto.setId(interview.getId());
        dto.setApplicationId(interview.getApplication().getApplicantId());
        dto.setScheduledById(interview.getScheduledBy().getId());
        dto.setScheduledByName(interview.getScheduledBy().getFullName());
        dto.setCandidateId(interview.getCandidate().getId());
        dto.setCandidateName(interview.getCandidate().getFullName());
        dto.setCandidateEmail(interview.getCandidate().getEmail());
        dto.setJobId(interview.getApplication().getJob().getId());
        dto.setJobTitle(interview.getApplication().getJob().getJobTitle());
        dto.setScheduledAt(interview.getScheduledAt());
        dto.setLocation(interview.getLocation());
        dto.setInterviewType(interview.getInterviewType());
        dto.setStatus(interview.getStatus());
        dto.setNotes(interview.getNotes());
        dto.setFeedback(interview.getFeedback());
        dto.setCalendarEventId(interview.getCalendarEventId());
        dto.setCalendarLink(interview.getCalendarLink());
        dto.setReminderSentAt(interview.getReminderSentAt());
        dto.setCreatedAt(interview.getCreatedAt());
        dto.setUpdatedAt(interview.getUpdatedAt());
        return dto;
    }
}






