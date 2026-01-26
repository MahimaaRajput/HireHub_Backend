package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDashboardDto {
    private Long totalApplications;
    private Long appliedCount;
    private Long interviewingCount;
    private Long offeredCount;
    private Long rejectedCount;
    private Long withdrawnCount;
    private List<ApplicationHistoryDto> recentApplications;
    private List<ApplicationHistoryDto> allApplications;
}

