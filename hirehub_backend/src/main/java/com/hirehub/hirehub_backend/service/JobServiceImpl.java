package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ApplicationDashboardDto;
import com.hirehub.hirehub_backend.dto.ApplicationHistoryDto;
import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.ApplicationDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.repository.ApplicantRepository;
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
    @Autowired
    private ApplicantRepository applicantRepository;

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
        
        // Link user if userId is provided in the DTO (we'll need to update ApplicantDto)
        // For now, we'll find user by email
        if (applicantDto.getEmail() != null) {
            userRepository.findByEmail(applicantDto.getEmail()).ifPresent(newApplicant::setUser);
        }
        
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
    public List<JobDto> getJobsByCompany(String company) {
        if (company == null || company.trim().isEmpty()) {
            return getAllJob();
        }
        // Use contains search for more flexible matching
        List<Job> jobs = jobRepository.findByCompanyContainingIgnoreCase(company.trim());
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

    @Override
    public List<ApplicantDto> getApplicantsForJob(Long jobId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        if (job.getApplicants() == null || job.getApplicants().isEmpty()) {
            return new ArrayList<>();
        }
        
        return job.getApplicants().stream()
                .map(Applicant::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void bulkUpdateApplicationStatus(com.hirehub.hirehub_backend.dto.BulkApplicationActionDto bulkAction) throws Exception {
        Job job = jobRepository.findById(bulkAction.getJobId())
                .orElseThrow(() -> new Exception("Job not found"));
        
        if (job.getApplicants() == null || job.getApplicants().isEmpty()) {
            throw new Exception("No applicants found for this job");
        }
        
        // Update status for all specified applicants
        int updatedCount = 0;
        for (Applicant applicant : job.getApplicants()) {
            if (bulkAction.getApplicantIds().contains(applicant.getApplicantId())) {
                applicant.setApplicationStatus(bulkAction.getNewStatus());
                
                // If status is INTERVIEWING, you might want to set interview time
                // For now, we'll leave it as is (can be set individually later)
                
                updatedCount++;
            }
        }
        
        if (updatedCount == 0) {
            throw new Exception("No matching applicants found to update");
        }
        
        jobRepository.save(job);
    }

    @Override
    public ApplicationDashboardDto getApplicationDashboard(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        // Get all applications for this user
        List<Applicant> allApplications = applicantRepository.findByUserId(userId);
        
        // Calculate statistics
        long totalApplications = allApplications.size();
        long appliedCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.APPLIED)
                .count();
        long interviewingCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.INTERVIEWING)
                .count();
        long offeredCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.OFFERED)
                .count();
        long rejectedCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.REJECTED)
                .count();
        long withdrawnCount = allApplications.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.WITHDRAWN)
                .count();
        
        // Convert to ApplicationHistoryDto
        List<ApplicationHistoryDto> allApplicationHistory = allApplications.stream()
                .map(app -> {
                    Job job = app.getJob();
                    return new ApplicationHistoryDto(
                            app.getApplicantId(),
                            job != null ? job.getId() : null,
                            job != null ? job.getJobTitle() : "Unknown",
                            job != null ? job.getCompany() : "Unknown",
                            job != null ? job.getLocation() : "Unknown",
                            app.getApplicationStatus(),
                            app.getTimestamp(),
                            app.getInterviewTime(),
                            app.getCoverLetter()
                    );
                })
                .sorted((a1, a2) -> a2.getAppliedDate().compareTo(a1.getAppliedDate())) // Sort by date, newest first
                .collect(Collectors.toList());
        
        // Get recent applications (last 10)
        List<ApplicationHistoryDto> recentApplications = allApplicationHistory.stream()
                .limit(10)
                .collect(Collectors.toList());
        
        return new ApplicationDashboardDto(
                totalApplications,
                appliedCount,
                interviewingCount,
                offeredCount,
                rejectedCount,
                withdrawnCount,
                recentApplications,
                allApplicationHistory
        );
    }

    @Override
    public void withdrawApplication(Long userId, Long applicationId) throws Exception {
        Applicant applicant = applicantRepository.findById(applicationId)
                .orElseThrow(() -> new Exception("Application not found"));
        
        // Verify the application belongs to the user
        if (applicant.getUser() == null || !applicant.getUser().getId().equals(userId)) {
            throw new Exception("You can only withdraw your own applications");
        }
        
        // Check if already withdrawn or in final state
        if (applicant.getApplicationStatus() == ApplicationStatus.WITHDRAWN) {
            throw new Exception("Application is already withdrawn");
        }
        
        if (applicant.getApplicationStatus() == ApplicationStatus.OFFERED) {
            throw new Exception("Cannot withdraw an application that has been offered");
        }
        
        applicant.setApplicationStatus(ApplicationStatus.WITHDRAWN);
        applicantRepository.save(applicant);
    }




}
