package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JobServiceImpl implements JobService{
    @Autowired
    private JobRepository jobRepository;

    @Override
    public JobDto postJob(JobDto jobDto) {

        Job job = new Job();
        job.setJobTitle(jobDto.getJobTitle());
        job.setCompany(jobDto.getCompany());
        job.setAbout(jobDto.getAbout());
        job.setExperience(jobDto.getExperience());
        job.setJobType(jobDto.getJobType());
        job.setLocation(jobDto.getLocation());
        job.setPackageOffered(jobDto.getPackageOffered());
        job.setDescription(jobDto.getDescription());
        job.setSkillsRequired(jobDto.getSkillsRequired());
        job.setJobStatus(jobDto.getJobStatus());
        job.setApplicants(new ArrayList<>());
        Job savedJob = jobRepository.save(job);
        return savedJob.toDto();
    }


}
