package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService{
    @Autowired
    private JobRepository jobRepository;

    @Override
    public JobDto postJob(JobDto jobDto) {
        return jobRepository.save(jobDto.toEntity()).toDto();
    }


}
