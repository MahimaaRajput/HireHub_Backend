package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDashboardDto {
    private CompanyDto company;
    private Long totalJobsPosted;
    private Long activeJobsCount;
    private Long totalApplications;
    private Long pendingApplications;
    private Long interviewingApplications;
    private Long offeredApplications;
    private List<JobDto> recentJobs;
}






