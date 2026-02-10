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
import com.hirehub.hirehub_backend.enums.WorkMode;
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
    
    @Autowired
    private NotificationService notificationService;

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
        job.setWorkMode(jobDto.getWorkMode());
        job.setApplicationDeadline(jobDto.getApplicationDeadline());
        job.setNumberOfOpenings(jobDto.getNumberOfOpenings());
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
        LocalDateTime now = LocalDateTime.now();
        return jobRepository.findAll().stream()
                .filter(j -> j.getApplicationDeadline() == null || !j.getApplicationDeadline().isBefore(now))
                .map(Job::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobDto getJobById(Long id) throws Exception {
        return jobRepository.findById(id).orElseThrow(()-> new Exception("job not found with this id")).toDto();
    }

    @Override
    public String applyJob(Long id, ApplicantDto applicantDto) throws Exception {
        Job job=jobRepository.findById(id).orElseThrow(()->new Exception("job not found with this id"));
        if (job.getApplicationDeadline() != null && LocalDateTime.now().isAfter(job.getApplicationDeadline())) {
            throw new Exception("Application deadline for this job has passed");
        }
        List<Applicant> applicants=job.getApplicants();
        if (applicants==null)applicants=new ArrayList<>();
        if (job.getNumberOfOpenings() != null && job.getNumberOfOpenings() > 0 && applicants.size() >= job.getNumberOfOpenings()) {
            throw new Exception("No openings left for this job");
        }
        
        // Check if user has already applied (by email or by user ID if linked)
        if(applicants.stream().anyMatch(x -> 
            x.getEmail().equals(applicantDto.getEmail()) || 
            (x.getUser() != null && applicantDto.getUserId() != null && x.getUser().getId().equals(applicantDto.getUserId()))
        )) {
            throw new Exception("You have already applied for this job");
        }
        
        applicantDto.setTimestamp(LocalDateTime.now());
        applicantDto.setApplicationStatus(ApplicationStatus.APPLIED);
        Applicant newApplicant = applicantDto.toEntity();
        newApplicant.setJob(job);
        
        // Link user by userId if provided, otherwise by email
        if (applicantDto.getUserId() != null) {
            User user = userRepository.findById(applicantDto.getUserId())
                    .orElseThrow(() -> new Exception("User not found"));
            newApplicant.setUser(user);
        } else if (applicantDto.getEmail() != null) {
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
        ApplicationStatus oldStatus = targetApplicant.getApplicationStatus();
        targetApplicant.setApplicationStatus(applicationDto.getApplicationStatus());

        if (ApplicationStatus.INTERVIEWING.equals(applicationDto.getApplicationStatus())) {
            targetApplicant.setInterviewTime(applicationDto.getInterviewTime());
        }
        
        // Send notification to applicant if status changed and user exists
        if (targetApplicant.getUser() != null && !oldStatus.equals(applicationDto.getApplicationStatus())) {
            try {
                notificationService.sendApplicationStatusNotification(
                        targetApplicant.getUser().getId(),
                        job.getJobTitle(),
                        applicationDto.getApplicationStatus().toString()
                );
            } catch (Exception e) {
                // Log error but don't fail status update
                System.err.println("Failed to send notification: " + e.getMessage());
            }
        }

        //  Save job with updated applicants
        jobRepository.save(job);
    }

    @Override
    public List<JobDto> searchJobsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllJob();
        }
        LocalDateTime now = LocalDateTime.now();
        List<Job> jobs = jobRepository.searchJobsByKeyword(keyword.trim());
        return jobs.stream()
                .filter(j -> j.getApplicationDeadline() == null || !j.getApplicationDeadline().isBefore(now))
                .map(Job::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDto> filterJobs(Long minSalary, Long maxSalary, String experience, 
                                   String location, String jobType, String category, WorkMode workMode, LocalDateTime startDate, LocalDateTime endDate) {
        List<Job> jobs = jobRepository.filterJobs(
                minSalary, 
                maxSalary, 
                experience != null ? experience.trim() : null,
                location != null ? location.trim() : null,
                jobType != null ? jobType.trim() : null,
                category != null ? category.trim() : null,
                workMode,
                startDate,
                endDate
        );
        LocalDateTime now = LocalDateTime.now();
        return jobs.stream()
                .filter(j -> j.getApplicationDeadline() == null || !j.getApplicationDeadline().isBefore(now))
                .map(Job::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDto> getJobsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllJob();
        }
        LocalDateTime now = LocalDateTime.now();
        List<Job> jobs = jobRepository.findByCategoryIgnoreCase(category.trim());
        return jobs.stream()
                .filter(j -> j.getApplicationDeadline() == null || !j.getApplicationDeadline().isBefore(now))
                .map(Job::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDto> getJobsByCompany(String company) {
        if (company == null || company.trim().isEmpty()) {
            return getAllJob();
        }
        // Use contains search for more flexible matching
        LocalDateTime now = LocalDateTime.now();
        List<Job> jobs = jobRepository.findByCompanyContainingIgnoreCase(company.trim());
        return jobs.stream()
                .filter(j -> j.getApplicationDeadline() == null || !j.getApplicationDeadline().isBefore(now))
                .map(Job::toDto)
                .collect(Collectors.toList());
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
        
        LocalDateTime now = LocalDateTime.now();
        return jobs.stream()
                .filter(j -> j.getApplicationDeadline() == null || !j.getApplicationDeadline().isBefore(now))
                .map(Job::toDto)
                .collect(Collectors.toList());
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

    @Override
    public void addApplicationNotes(com.hirehub.hirehub_backend.dto.ApplicationNoteDto noteDto) throws Exception {
        Job job = jobRepository.findById(noteDto.getJobId())
                .orElseThrow(() -> new Exception("Job not found"));
        
        Applicant applicant = job.getApplicants().stream()
                .filter(app -> app.getApplicantId().equals(noteDto.getApplicantId()))
                .findFirst()
                .orElseThrow(() -> new Exception("Applicant not found for this job"));
        
        applicant.setRecruiterNotes(noteDto.getNotes());
        jobRepository.save(job);
    }

    @Override
    public void incrementApplicationView(Long jobId, Long applicantId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        Applicant applicant = job.getApplicants().stream()
                .filter(app -> app.getApplicantId().equals(applicantId))
                .findFirst()
                .orElseThrow(() -> new Exception("Applicant not found for this job"));
        
        // Increment view count
        if (applicant.getViewCount() == null) {
            applicant.setViewCount(0);
        }
        applicant.setViewCount(applicant.getViewCount() + 1);
        jobRepository.save(job);
    }

    @Override
    public void toggleShortlistApplication(Long jobId, Long applicantId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        Applicant applicant = job.getApplicants().stream()
                .filter(app -> app.getApplicantId().equals(applicantId))
                .findFirst()
                .orElseThrow(() -> new Exception("Applicant not found for this job"));
        
        // Toggle shortlist status
        boolean newShortlistStatus = applicant.getIsShortlisted() == null || !applicant.getIsShortlisted();
        applicant.setIsShortlisted(newShortlistStatus);
        
        if (newShortlistStatus) {
            applicant.setShortlistedAt(LocalDateTime.now());
        } else {
            applicant.setShortlistedAt(null);
        }
        
        jobRepository.save(job);
    }

    @Override
    public com.hirehub.hirehub_backend.dto.ApplicationAnalyticsDto getApplicationAnalytics(Long jobId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        List<Applicant> applicants = job.getApplicants();
        if (applicants == null) {
            applicants = new ArrayList<>();
        }
        
        // Calculate statistics
        Long totalApplications = (long) applicants.size();
        Long totalViews = applicants.stream()
                .mapToLong(app -> app.getViewCount() != null ? app.getViewCount() : 0)
                .sum();
        Long shortlistedCount = applicants.stream()
                .filter(app -> app.getIsShortlisted() != null && app.getIsShortlisted())
                .count();
        Long interviewedCount = applicants.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.INTERVIEWING)
                .count();
        Long offeredCount = applicants.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.OFFERED)
                .count();
        Long rejectedCount = applicants.stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.REJECTED)
                .count();
        
        // Get most viewed applications (top 10)
        List<ApplicantDto> mostViewedApplications = applicants.stream()
                .sorted((a1, a2) -> {
                    int views1 = a1.getViewCount() != null ? a1.getViewCount() : 0;
                    int views2 = a2.getViewCount() != null ? a2.getViewCount() : 0;
                    return Integer.compare(views2, views1); // Descending order
                })
                .limit(10)
                .map(Applicant::toDto)
                .collect(Collectors.toList());
        
        // Get shortlisted applications
        List<ApplicantDto> shortlistedApplications = applicants.stream()
                .filter(app -> app.getIsShortlisted() != null && app.getIsShortlisted())
                .sorted((a1, a2) -> {
                    if (a1.getShortlistedAt() == null) return 1;
                    if (a2.getShortlistedAt() == null) return -1;
                    return a2.getShortlistedAt().compareTo(a1.getShortlistedAt()); // Newest first
                })
                .map(Applicant::toDto)
                .collect(Collectors.toList());
        
        return new com.hirehub.hirehub_backend.dto.ApplicationAnalyticsDto(
                totalApplications,
                totalViews,
                shortlistedCount,
                interviewedCount,
                offeredCount,
                rejectedCount,
                mostViewedApplications,
                shortlistedApplications
        );
    }




}
