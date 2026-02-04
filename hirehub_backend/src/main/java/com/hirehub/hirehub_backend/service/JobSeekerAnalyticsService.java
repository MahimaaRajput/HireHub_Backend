package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobSeekerAnalyticsDto;

public interface JobSeekerAnalyticsService {
    JobSeekerAnalyticsDto getJobSeekerAnalytics(Long jobSeekerId) throws Exception;
}

