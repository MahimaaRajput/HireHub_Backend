package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDto {
    private Long id;
    private Long applicationId;
    private Long scheduledById;
    private String scheduledByName;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private Long jobId;
    private String jobTitle;
    private LocalDateTime scheduledAt;
    private String location;
    private String interviewType; // IN_PERSON, VIDEO_CALL, PHONE_CALL
    private String status; // SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, RESCHEDULED
    private String notes;
    private String feedback;
    private String calendarEventId;
    private String calendarLink;
    private LocalDateTime reminderSentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


