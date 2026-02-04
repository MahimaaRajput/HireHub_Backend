package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDto {
    // User statistics
    private Long totalUsers;
    private Long totalJobSeekers; // Users with role USER
    private Long totalRecruiters; // Users with role RECRUITER
    private Long activeUsers; // Users who logged in recently (last 30 days)
    private Long newUsersLastMonth; // New users registered in last 30 days

    // Job statistics
    private Long totalJobs;
    private Long activeJobs; // Jobs with status OPEN
    private Long closedJobs; // Jobs with status CLOSED
    private Long jobsPostedLastMonth; // Jobs posted in last 30 days

    // Application statistics
    private Long totalApplications;
    private Long applicationsLastMonth; // Applications submitted in last 30 days
    private Map<String, Long> applicationsByStatus; // Count by status (APPLIED, INTERVIEWING, etc.)

    // Company statistics
    private Long totalCompanies;
    private Long verifiedCompanies;
    private Long pendingVerificationCompanies;

    // Growth metrics
    private Double userGrowthRate; // Percentage growth in users
    private Double jobGrowthRate; // Percentage growth in jobs
    private Double applicationGrowthRate; // Percentage growth in applications
}

