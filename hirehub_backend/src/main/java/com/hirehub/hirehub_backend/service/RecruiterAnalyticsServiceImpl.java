package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobAnalyticsDto;
import com.hirehub.hirehub_backend.dto.RecruiterAnalyticsDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.JobView;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.repository.ApplicantRepository;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.JobViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecruiterAnalyticsServiceImpl implements RecruiterAnalyticsService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobViewRepository jobViewRepository;

    @Override
    public RecruiterAnalyticsDto getRecruiterAnalytics(Long recruiterId) throws Exception {
        RecruiterAnalyticsDto analytics = new RecruiterAnalyticsDto();

        // Get all jobs posted by this recruiter
        List<Job> recruiterJobs = jobRepository.findByPostedBy(recruiterId);
        
        analytics.setTotalJobsPosted((long) recruiterJobs.size());
        
        long activeJobs = recruiterJobs.stream()
                .filter(j -> "OPEN".equalsIgnoreCase(j.getJobStatus()))
                .count();
        analytics.setActiveJobs(activeJobs);

        // Calculate date ranges
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Get all job IDs
        List<Long> jobIds = recruiterJobs.stream()
                .map(Job::getId)
                .collect(Collectors.toList());

        // Get all applications for these jobs
        List<Applicant> allApplications = new ArrayList<>();
        for (Long jobId : jobIds) {
            allApplications.addAll(applicantRepository.findByJobId(jobId));
        }
        
        analytics.setTotalApplications((long) allApplications.size());

        // Calculate total views
        long totalViews = 0L;
        for (Long jobId : jobIds) {
            Long views = jobViewRepository.countViewsByJobId(jobId);
            if (views != null) {
                totalViews += views;
            }
        }
        analytics.setTotalJobViews(totalViews);

        // Applications by status
        Map<String, Long> applicationsByStatus = allApplications.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getApplicationStatus() != null ? a.getApplicationStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));
        analytics.setApplicationsByStatus(applicationsByStatus);

        // Applications in last 7 and 30 days
        long applicationsLast7Days = allApplications.stream()
                .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(sevenDaysAgo))
                .count();
        analytics.setApplicationsLast7Days(applicationsLast7Days);

        long applicationsLast30Days = allApplications.stream()
                .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(thirtyDaysAgo))
                .count();
        analytics.setApplicationsLast30Days(applicationsLast30Days);

        // Views in last 7 and 30 days
        long viewsLast7Days = 0L;
        long viewsLast30Days = 0L;
        for (Long jobId : jobIds) {
            List<JobView> jobViews = jobViewRepository.findAll().stream()
                    .filter(jv -> jv.getJob() != null && jv.getJob().getId().equals(jobId))
                    .collect(Collectors.toList());
            
            viewsLast7Days += jobViews.stream()
                    .filter(jv -> jv.getViewedAt() != null && jv.getViewedAt().isAfter(sevenDaysAgo))
                    .count();
            
            viewsLast30Days += jobViews.stream()
                    .filter(jv -> jv.getViewedAt() != null && jv.getViewedAt().isAfter(thirtyDaysAgo))
                    .count();
        }
        analytics.setViewsLast7Days(viewsLast7Days);
        analytics.setViewsLast30Days(viewsLast30Days);

        // Per job analytics
        List<JobAnalyticsDto> jobAnalyticsList = new ArrayList<>();
        for (Job job : recruiterJobs) {
            JobAnalyticsDto jobAnalytics = calculateJobAnalytics(job);
            jobAnalyticsList.add(jobAnalytics);
        }
        analytics.setJobAnalytics(jobAnalyticsList);

        // Top performing jobs (by applications)
        List<JobAnalyticsDto> topPerforming = jobAnalyticsList.stream()
                .sorted((a, b) -> Long.compare(b.getTotalApplications(), a.getTotalApplications()))
                .limit(10)
                .collect(Collectors.toList());
        analytics.setTopPerformingJobs(topPerforming);

        // Most viewed jobs
        List<JobAnalyticsDto> mostViewed = jobAnalyticsList.stream()
                .sorted((a, b) -> Long.compare(b.getTotalViews(), a.getTotalViews()))
                .limit(10)
                .collect(Collectors.toList());
        analytics.setMostViewedJobs(mostViewed);

        return analytics;
    }

    @Override
    public RecruiterAnalyticsDto getJobAnalytics(Long jobId, Long recruiterId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));

        // Verify recruiter owns this job
        if (job.getPostedBy() == null || !job.getPostedBy().equals(recruiterId.toString())) {
            throw new Exception("You don't have permission to view analytics for this job");
        }

        RecruiterAnalyticsDto analytics = new RecruiterAnalyticsDto();
        
        // Get job analytics
        JobAnalyticsDto jobAnalytics = calculateJobAnalytics(job);
        analytics.setJobAnalytics(Collections.singletonList(jobAnalytics));
        
        // Get applications for this job
        List<Applicant> applications = applicantRepository.findByJobId(jobId);
        analytics.setTotalApplications((long) applications.size());

        // Applications by status
        Map<String, Long> applicationsByStatus = applications.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getApplicationStatus() != null ? a.getApplicationStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));
        analytics.setApplicationsByStatus(applicationsByStatus);

        analytics.setTotalJobViews(jobAnalytics.getTotalViews());
        analytics.setTotalJobsPosted(1L);
        analytics.setActiveJobs("OPEN".equalsIgnoreCase(job.getJobStatus()) ? 1L : 0L);

        return analytics;
    }

    private JobAnalyticsDto calculateJobAnalytics(Job job) {
        JobAnalyticsDto analytics = new JobAnalyticsDto();
        analytics.setJobId(job.getId());
        analytics.setJobTitle(job.getJobTitle());
        analytics.setCompany(job.getCompany());
        analytics.setLocation(job.getLocation());

        // Get views
        Long views = jobViewRepository.countViewsByJobId(job.getId());
        analytics.setTotalViews(views != null ? views : 0L);

        // Get applications
        List<Applicant> applications = applicantRepository.findByJobId(job.getId());
        analytics.setTotalApplications((long) applications.size());

        // Applications by status
        long shortlisted = applications.stream()
                .filter(a -> a.getIsShortlisted() != null && a.getIsShortlisted())
                .count();
        analytics.setShortlistedApplications(shortlisted);

        long interviewing = applications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.INTERVIEWING)
                .count();
        analytics.setInterviewingApplications(interviewing);

        long offered = applications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.OFFERED)
                .count();
        analytics.setOfferedApplications(offered);

        long rejected = applications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.REJECTED)
                .count();
        analytics.setRejectedApplications(rejected);

        // Calculate rates
        if (analytics.getTotalViews() > 0) {
            double applicationRate = ((double) analytics.getTotalApplications() / analytics.getTotalViews()) * 100.0;
            analytics.setApplicationRate(applicationRate);
        } else {
            analytics.setApplicationRate(0.0);
        }

        if (analytics.getTotalApplications() > 0) {
            double conversionRate = ((double) analytics.getOfferedApplications() / analytics.getTotalApplications()) * 100.0;
            analytics.setConversionRate(conversionRate);
        } else {
            analytics.setConversionRate(0.0);
        }

        return analytics;
    }
}

