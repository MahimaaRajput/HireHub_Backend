package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterAnalyticsDto {
    // Overall statistics
    private Long totalJobsPosted;
    private Long activeJobs;
    private Long totalApplications;
    private Long totalJobViews;

    // Per job analytics
    private List<JobAnalyticsDto> jobAnalytics;

    // Application statistics by status
    private Map<String, Long> applicationsByStatus;

    // Top performing jobs (by applications)
    private List<JobAnalyticsDto> topPerformingJobs;

    // Jobs with most views
    private List<JobAnalyticsDto> mostViewedJobs;

    // Time-based statistics
    private Long applicationsLast7Days;
    private Long applicationsLast30Days;
    private Long viewsLast7Days;
    private Long viewsLast30Days;
}



