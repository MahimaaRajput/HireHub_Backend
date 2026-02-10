package com.hirehub.hirehub_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // User who receives the notification
    
    private String title; // Notification title
    private String message; // Notification message/content
    private String type; // Type: APPLICATION_STATUS, NEW_JOB, JOB_ALERT, SYSTEM, etc.
    private String relatedEntityType; // JOB, APPLICATION, COMPANY, etc.
    private Long relatedEntityId; // ID of related entity (jobId, applicationId, etc.)
    
    private Boolean isRead; // Whether notification has been read
    private Boolean isEmailSent; // Whether email notification was sent
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime readAt; // When notification was read
}








