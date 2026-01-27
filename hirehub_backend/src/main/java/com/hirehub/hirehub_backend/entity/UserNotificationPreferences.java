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
    
    // Email notification preferences
    private Boolean emailApplicationStatus; // Notify when application status changes
    private Boolean emailNewJobs; // Notify about new matching jobs
    private Boolean emailJobAlerts; // Notify about job alerts
    private Boolean emailSystemUpdates; // System/account updates
    
    // In-app notification preferences
    private Boolean inAppApplicationStatus; // In-app notifications for application status
    private Boolean inAppNewJobs; // In-app notifications for new jobs
    private Boolean inAppJobAlerts; // In-app notifications for job alerts
    private Boolean inAppSystemUpdates; // In-app system notifications
    
    // Default: all enabled
    public UserNotificationPreferences() {
        this.emailApplicationStatus = true;
        this.emailNewJobs = true;
        this.emailJobAlerts = true;
        this.emailSystemUpdates = true;
        this.inAppApplicationStatus = true;
        this.inAppNewJobs = true;
        this.inAppJobAlerts = true;
        this.inAppSystemUpdates = true;
    }
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

