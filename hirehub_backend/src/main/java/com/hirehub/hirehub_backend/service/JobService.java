package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;

import java.util.List;

public interface  JobService {
     JobDto postJob(JobDto jobDto);
     List<JobDto>getAllJob();
}
