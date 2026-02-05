package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAnalyticsDto {
    private Long jobId;
    private String jobTitle;
    private String company;
    private String location;
    private Long totalViews;
    private Long totalApplications;
    private Long shortlistedApplications;
    private Long interviewingApplications;
    private Long offeredApplications;
    private Long rejectedApplications;
    private Double applicationRate; // Applications per view percentage
    private Double conversionRate; // Offers per application percentage
}


