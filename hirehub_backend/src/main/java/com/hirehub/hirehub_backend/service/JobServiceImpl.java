package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<JobDto> getAllJob() {
        return jobRepository.findAll().stream().map(Job::toDto).collect(Collectors.toList());
    }

    @Override
    public JobDto getJobById(Long id) throws Exception {
        return jobRepository.findById(id).orElseThrow(()-> new Exception("job not found with this id")).toDto();
    }

    @Override
    public String applyJob(Long id, ApplicantDto applicantDto) throws Exception {
        Job job=jobRepository.findById(id).orElseThrow(()->new Exception("job not found with this id"));
        List<Applicant> applicants=job.getApplicants();
        if (applicants==null)applicants=new ArrayList<>();
        if(applicants.stream().anyMatch(x -> x.getEmail().equals(applicantDto.getEmail()))) {
            throw new Exception("You have already applied for this job");
        }
        applicantDto.setTimestamp(LocalDateTime.now());
        applicantDto.setApplicationStatus(ApplicationStatus.APPLIED);
        Applicant newApplicant = applicantDto.toEntity();
        newApplicant.setJob(job);
        applicants.add(newApplicant);
        job.setApplicants(applicants);
        jobRepository.save(job);
        return "Applied successfully";
    }


}
