package com.hirehub.hirehub_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // User who created this alert
    
    private String alertName; // Name of the alert (e.g., "Software Engineer Jobs")
    
    // Search criteria
    private String keywords; // Job title, skills, or company keywords
    @ElementCollection
    private List<String> locations; // Preferred locations
    private Long minSalary; // Minimum salary
    private Long maxSalary; // Maximum salary
    private String experience; // Experience level
    private String jobType; // Full-time, Part-time, Contract, etc.
    private String category; // Industry/Category
    
    // Alert settings
    private Boolean isActive; // Is alert active
    private String frequency; // DAILY, WEEKLY, INSTANT
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastSentAt; // When was the last alert sent
}
