package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.ApplicationDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

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
        job.setCategory(jobDto.getCategory());
        job.setApplicants(new ArrayList<>());

//        Long recruiterId = JwtProvider.getUserIdFromToken();
        User recruiter = userRepository.findById(jobDto.getPostedBy())
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        job.setPostedBy(recruiter.getId());
        Job savedJob = jobRepository.save(job);

        // convert back to DTO
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

    @Override
    public List<JobDto> getPostedJobs(Long id) throws Exception {
        List<Job> found = jobRepository.findByPostedBy(id);
        if (found.isEmpty())
        {
            throw new Exception("job is not posted by this id");
        }
            return found.stream().map(Job::toDto).collect(Collectors.toList());

    }

    @Override
    public void changeAppStatus(ApplicationDto applicationDto) throws Exception {
        Job job = jobRepository.findById(applicationDto.getId())
                .orElseThrow(() -> new Exception("Job not found with this id"));

        Applicant targetApplicant = job.getApplicants().stream()
                .filter(app -> applicationDto.getApplicantId().equals(app.getApplicantId()))
                .findFirst()
                .orElseThrow(() -> new Exception("Applicant not found for this job"));

        //  Update applicant fields
        targetApplicant.setApplicationStatus(applicationDto.getApplicationStatus());

        if (ApplicationStatus.INTERVIEWING.equals(applicationDto.getApplicationStatus())) {
            targetApplicant.setInterviewTime(applicationDto.getInterviewTime());
        }

        //  Save job with updated applicants
        jobRepository.save(job);
    }

    @Override
    public List<JobDto> searchJobsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllJob();
        }
        List<Job> jobs = jobRepository.searchJobsByKeyword(keyword.trim());
        return jobs.stream().map(Job::toDto).collect(Collectors.toList());
    }

    @Override
    public List<JobDto> filterJobs(Long minSalary, Long maxSalary, String experience, 
                                   String location, String jobType, String category, LocalDateTime startDate, LocalDateTime endDate) {
        List<Job> jobs = jobRepository.filterJobs(
                minSalary, 
                maxSalary, 
                experience != null ? experience.trim() : null,
                location != null ? location.trim() : null,
                jobType != null ? jobType.trim() : null,
                category != null ? category.trim() : null,
                startDate,
                endDate
        );
        return jobs.stream().map(Job::toDto).collect(Collectors.toList());
    }

    @Override
    public List<JobDto> getJobsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllJob();
        }
        List<Job> jobs = jobRepository.findByCategoryIgnoreCase(category.trim());
        return jobs.stream().map(Job::toDto).collect(Collectors.toList());
    }

    @Override
    public List<JobDto> getAllJobsSorted(String sortBy) {
        List<Job> jobs;
        
        if (sortBy == null || sortBy.trim().isEmpty()) {
            // Default: sort by date (newest first)
            jobs = jobRepository.findAllByOrderByCreatedAtDesc();
        } else {
            switch (sortBy.toLowerCase()) {
                case "date":
                case "date_desc":
                    jobs = jobRepository.findAllByOrderByCreatedAtDesc();
                    break;
                case "date_asc":
                    jobs = jobRepository.findAllByOrderByCreatedAtAsc();
                    break;
                case "salary":
                case "salary_desc":
                    jobs = jobRepository.findAllByOrderByPackageOfferedDesc();
                    break;
                case "salary_asc":
                    jobs = jobRepository.findAllByOrderByPackageOfferedAsc();
                    break;
                case "relevance":
                default:
                    // For relevance, we'll sort by date (newest first) as default
                    // In a real scenario, relevance would be calculated based on search terms, profile match, etc.
                    jobs = jobRepository.findAllByOrderByCreatedAtDesc();
                    break;
            }
        }
        
        return jobs.stream().map(Job::toDto).collect(Collectors.toList());
    }




}
