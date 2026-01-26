package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.ApplicationDto;
import com.hirehub.hirehub_backend.dto.JobDto;

import java.time.LocalDateTime;
import java.util.List;

public interface  JobService {
     JobDto postJob(JobDto jobDto);
     List<JobDto>getAllJob();

    JobDto getJobById(Long id)throws Exception;

    String applyJob(Long id, ApplicantDto applicantDto) throws Exception;

    List<JobDto> getPostedJobs(Long id) throws Exception;

    void changeAppStatus(ApplicationDto applicationDto)throws Exception;
    
    List<JobDto> searchJobsByKeyword(String keyword);
    
    List<JobDto> filterJobs(Long minSalary, Long maxSalary, String experience, 
                           String location, String jobType, String category, LocalDateTime startDate, LocalDateTime endDate);
    
    List<JobDto> getJobsByCategory(String category);
    
    List<JobDto> getAllJobsSorted(String sortBy);
    
    List<JobDto> getJobsByCompany(String company);
    
    List<ApplicantDto> getApplicantsForJob(Long jobId) throws Exception;
    
    void bulkUpdateApplicationStatus(com.hirehub.hirehub_backend.dto.BulkApplicationActionDto bulkAction) throws Exception;
    
    com.hirehub.hirehub_backend.dto.ApplicationDashboardDto getApplicationDashboard(Long userId) throws Exception;
    void withdrawApplication(Long userId, Long applicationId) throws Exception;
}

