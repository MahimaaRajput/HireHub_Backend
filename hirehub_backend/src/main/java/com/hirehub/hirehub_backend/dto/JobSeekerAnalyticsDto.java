package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerAnalyticsDto {
    // Overall statistics
    private Long totalApplications;
    private Long activeApplications; // Applications in progress (not rejected/withdrawn)
    private Double successRate; // Percentage of applications that resulted in offers
    private Double interviewRate; // Percentage of applications that reached interview stage
    private Double responseRate; // Percentage of applications that got any response

    // Applications by status
    private Map<String, Long> applicationsByStatus;

    // Applications by job category
    private Map<String, Long> applicationsByCategory;

    // Time-based statistics
    private Long applicationsLast7Days;
    private Long applicationsLast30Days;
    private Long interviewsLast30Days;
    private Long offersLast30Days;

    // Success metrics
    private Long totalInterviews;
    private Long totalOffers;
    private Long totalRejections;
    private Long totalWithdrawn;

    // Average response time (days from application to first response)
    private Double averageResponseTime;

    // Top applied categories
    private List<CategoryApplicationDto> topAppliedCategories;

    // Application timeline
    private List<ApplicationTimelineDto> recentApplications;
}



