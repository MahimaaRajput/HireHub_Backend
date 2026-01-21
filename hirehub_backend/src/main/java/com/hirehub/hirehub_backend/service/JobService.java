package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.ApplicationDto;
import com.hirehub.hirehub_backend.dto.JobDto;

import java.util.List;

public interface  JobService {
     JobDto postJob(JobDto jobDto);
     List<JobDto>getAllJob();

    JobDto getJobById(Long id)throws Exception;

    String applyJob(Long id, ApplicantDto applicantDto) throws Exception;

    List<JobDto> getPostedJobs(Long id) throws Exception;

    void changeAppStatus(ApplicationDto applicationDto)throws Exception;
    
    List<JobDto> searchJobsByKeyword(String keyword);
}

