package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationAnalyticsDto {
    private Long totalApplications;
    private Long totalViews;
    private Long shortlistedCount;
    private Long interviewedCount;
    private Long offeredCount;
    private Long rejectedCount;
    private List<ApplicantDto> mostViewedApplications; // Top 10 most viewed
    private List<ApplicantDto> shortlistedApplications;
}

