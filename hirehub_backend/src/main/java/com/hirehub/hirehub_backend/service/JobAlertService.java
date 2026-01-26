package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobAlertDto;
import com.hirehub.hirehub_backend.dto.JobDto;

import java.util.List;

public interface JobAlertService {
    JobAlertDto createJobAlert(Long userId, JobAlertDto alertDto) throws Exception;
    JobAlertDto updateJobAlert(Long userId, Long alertId, JobAlertDto alertDto) throws Exception;
    void deleteJobAlert(Long userId, Long alertId) throws Exception;
    List<JobAlertDto> getUserJobAlerts(Long userId);
    JobAlertDto getJobAlertById(Long userId, Long alertId) throws Exception;
    void toggleJobAlert(Long userId, Long alertId) throws Exception;
    List<JobDto> getMatchingJobsForAlert(Long alertId) throws Exception;
}
