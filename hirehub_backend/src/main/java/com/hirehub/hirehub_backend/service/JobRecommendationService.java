package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;

import java.util.List;

public interface JobRecommendationService {
    void recordJobView(Long userId, Long jobId) throws Exception;
    List<JobDto> getRecentlyViewedJobs(Long userId, Integer limit) throws Exception;
    List<JobDto> getPopularJobsInArea(String location, Integer limit) throws Exception;
    List<JobDto> getSimilarJobs(Long jobId, Integer limit) throws Exception;
    List<JobDto> getJobRecommendationsBasedOnProfile(Long userId, Integer limit) throws Exception;
}



