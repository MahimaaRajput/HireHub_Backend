package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;

import java.util.List;

public interface SavedJobService {
    String saveJob(Long userId, Long jobId) throws Exception;
    String unsaveJob(Long userId, Long jobId) throws Exception;
    List<JobDto> getSavedJobs(Long userId);
    boolean isJobSaved(Long userId, Long jobId);
}
