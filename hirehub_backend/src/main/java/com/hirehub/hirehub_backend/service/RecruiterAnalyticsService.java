package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.RecruiterAnalyticsDto;

public interface RecruiterAnalyticsService {
    RecruiterAnalyticsDto getRecruiterAnalytics(Long recruiterId) throws Exception;
    RecruiterAnalyticsDto getJobAnalytics(Long jobId, Long recruiterId) throws Exception;
}


