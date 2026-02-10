package com.hirehub.hirehub_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Applicant application; // Link to the job application

    @ManyToOne
    @JoinColumn(name = "scheduled_by_id", nullable = false)
    private User scheduledBy; // Recruiter who scheduled the interview

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate; // Candidate being interviewed

    private LocalDateTime scheduledAt; // Interview date and time

    private String location; // Physical location or video call link
    private String interviewType; // IN_PERSON, VIDEO_CALL, PHONE_CALL
    private String status; // SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, RESCHEDULED

    @Column(length = 2000)
    private String notes; // Interview notes or instructions

    @Column(length = 2000)
    private String feedback; // Interview feedback (after completion)

    private String calendarEventId; // ID from external calendar (Google Calendar, Outlook, etc.)
    private String calendarLink; // Link to calendar event

    private LocalDateTime reminderSentAt; // When reminder was sent

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}






