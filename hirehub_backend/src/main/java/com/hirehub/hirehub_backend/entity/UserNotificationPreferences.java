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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class UserNotificationPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Email notification preferences (default: all enabled)
    private Boolean emailApplicationStatus = true; // Notify when application status changes
    private Boolean emailNewJobs = true; // Notify about new matching jobs
    private Boolean emailJobAlerts = true; // Notify about job alerts
    private Boolean emailSystemUpdates = true; // System/account updates
    
    // In-app notification preferences
    private Boolean inAppApplicationStatus = true; // In-app notifications for application status
    private Boolean inAppNewJobs = true; // In-app notifications for new jobs
    private Boolean inAppJobAlerts = true; // In-app notifications for job alerts
    private Boolean inAppSystemUpdates = true; // In-app system notifications

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}








