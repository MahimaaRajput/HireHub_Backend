package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.AdminDashboardDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Company;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.enums.Role;
import com.hirehub.hirehub_backend.enums.VerificationStatus;
import com.hirehub.hirehub_backend.repository.ApplicantRepository;
import com.hirehub.hirehub_backend.repository.CompanyRepository;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public AdminDashboardDto getAdminDashboard() throws Exception {
        AdminDashboardDto dashboard = new AdminDashboardDto();

        // Calculate date 30 days ago
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);

        // User statistics
        List<User> allUsers = userRepository.findAll();
        dashboard.setTotalUsers((long) allUsers.size());
        
        long jobSeekers = allUsers.stream()
                .filter(u -> u.getRole() == Role.USER)
                .count();
        dashboard.setTotalJobSeekers(jobSeekers);
        
        long recruiters = allUsers.stream()
                .filter(u -> u.getRole() == Role.RECRUITER)
                .count();
        dashboard.setTotalRecruiters(recruiters);

        // For active users, we'll count all users (since we don't have last login tracking)
        // In a real system, you'd query users with lastLoginDate >= thirtyDaysAgo
        dashboard.setActiveUsers((long) allUsers.size());

        // New users in last month (approximate - using all users since we don't have createdAt)
        // In a real system, you'd query users with createdAt >= thirtyDaysAgo
        dashboard.setNewUsersLastMonth((long) allUsers.size());

        // Job statistics
        List<Job> allJobs = jobRepository.findAll();
        dashboard.setTotalJobs((long) allJobs.size());
        
        long activeJobs = allJobs.stream()
                .filter(j -> "OPEN".equalsIgnoreCase(j.getJobStatus()))
                .count();
        dashboard.setActiveJobs(activeJobs);
        
        long closedJobs = allJobs.stream()
                .filter(j -> "CLOSED".equalsIgnoreCase(j.getJobStatus()))
                .count();
        dashboard.setClosedJobs(closedJobs);

        // Jobs posted in last month
        long jobsLastMonth = allJobs.stream()
                .filter(j -> j.getCreatedAt() != null && j.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
        dashboard.setJobsPostedLastMonth(jobsLastMonth);

        // Application statistics
        List<Applicant> allApplications = applicantRepository.findAll();
        dashboard.setTotalApplications((long) allApplications.size());
        
        long applicationsLastMonth = allApplications.stream()
                .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(thirtyDaysAgo))
                .count();
        dashboard.setApplicationsLastMonth(applicationsLastMonth);

        // Applications by status
        Map<String, Long> applicationsByStatus = allApplications.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getApplicationStatus() != null ? a.getApplicationStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));
        dashboard.setApplicationsByStatus(applicationsByStatus);

        // Company statistics
        List<Company> allCompanies = companyRepository.findAll();
        dashboard.setTotalCompanies((long) allCompanies.size());
        
        long verifiedCompanies = allCompanies.stream()
                .filter(c -> c.getVerificationStatus() == VerificationStatus.VERIFIED)
                .count();
        dashboard.setVerifiedCompanies(verifiedCompanies);
        
        long pendingCompanies = allCompanies.stream()
                .filter(c -> c.getVerificationStatus() == VerificationStatus.PENDING)
                .count();
        dashboard.setPendingVerificationCompanies(pendingCompanies);

        // Growth rates (comparing last 30 days with previous 30 days)
        // Users growth
        long usersLastMonth = allUsers.size(); // Approximate
        long usersPreviousMonth = 0L; // Would need historical data
        dashboard.setUserGrowthRate(calculateGrowthRate(usersPreviousMonth, usersLastMonth));

        // Jobs growth
        long jobsPreviousMonth = allJobs.stream()
                .filter(j -> j.getCreatedAt() != null && 
                           j.getCreatedAt().isAfter(sixtyDaysAgo) && 
                           j.getCreatedAt().isBefore(thirtyDaysAgo))
                .count();
        dashboard.setJobGrowthRate(calculateGrowthRate(jobsPreviousMonth, jobsLastMonth));

        // Applications growth
        long applicationsPreviousMonth = allApplications.stream()
                .filter(a -> a.getTimestamp() != null && 
                           a.getTimestamp().isAfter(sixtyDaysAgo) && 
                           a.getTimestamp().isBefore(thirtyDaysAgo))
                .count();
        dashboard.setApplicationGrowthRate(calculateGrowthRate(applicationsPreviousMonth, applicationsLastMonth));

        return dashboard;
    }

    private Double calculateGrowthRate(long previous, long current) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double) (current - previous) / previous) * 100.0;
    }
}





