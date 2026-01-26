package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;

import java.util.List;

public interface SavedJobService {
    void saveJob(Long userId, Long jobId) throws Exception;
    void unsaveJob(Long userId, Long jobId) throws Exception;
    List<JobDto> getSavedJobs(Long userId);
    boolean isJobSaved(Long userId, Long jobId);
}

