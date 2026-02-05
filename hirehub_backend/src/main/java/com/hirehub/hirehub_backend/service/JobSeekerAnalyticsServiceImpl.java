package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ApplicationTimelineDto;
import com.hirehub.hirehub_backend.dto.CategoryApplicationDto;
import com.hirehub.hirehub_backend.dto.JobSeekerAnalyticsDto;
import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobSeekerAnalyticsServiceImpl implements JobSeekerAnalyticsService {

    @Autowired
    private ApplicantRepository applicantRepository;

    @Override
    public JobSeekerAnalyticsDto getJobSeekerAnalytics(Long jobSeekerId) throws Exception {
        JobSeekerAnalyticsDto analytics = new JobSeekerAnalyticsDto();

        // Get all applications for this job seeker
        List<Applicant> allApplications = applicantRepository.findByUserId(jobSeekerId);
        
        analytics.setTotalApplications((long) allApplications.size());

        // Calculate date ranges
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Applications in last 7 and 30 days
        long applicationsLast7Days = allApplications.stream()
                .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(sevenDaysAgo))
                .count();
        analytics.setApplicationsLast7Days(applicationsLast7Days);

        long applicationsLast30Days = allApplications.stream()
                .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(thirtyDaysAgo))
                .count();
        analytics.setApplicationsLast30Days(applicationsLast30Days);

        // Active applications (not rejected or withdrawn)
        long activeApplications = allApplications.stream()
                .filter(a -> a.getApplicationStatus() != ApplicationStatus.REJECTED && 
                           a.getApplicationStatus() != ApplicationStatus.WITHDRAWN)
                .count();
        analytics.setActiveApplications(activeApplications);

        // Applications by status
        Map<String, Long> applicationsByStatus = allApplications.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getApplicationStatus() != null ? a.getApplicationStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));
        analytics.setApplicationsByStatus(applicationsByStatus);

        // Count by status
        long totalInterviews = allApplications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.INTERVIEWING)
                .count();
        analytics.setTotalInterviews(totalInterviews);

        long totalOffers = allApplications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.OFFERED)
                .count();
        analytics.setTotalOffers(totalOffers);

        long totalRejections = allApplications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.REJECTED)
                .count();
        analytics.setTotalRejections(totalRejections);

        long totalWithdrawn = allApplications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.WITHDRAWN)
                .count();
        analytics.setTotalWithdrawn(totalWithdrawn);

        // Interviews and offers in last 30 days
        long interviewsLast30Days = allApplications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.INTERVIEWING &&
                           a.getTimestamp() != null && a.getTimestamp().isAfter(thirtyDaysAgo))
                .count();
        analytics.setInterviewsLast30Days(interviewsLast30Days);

        long offersLast30Days = allApplications.stream()
                .filter(a -> a.getApplicationStatus() == ApplicationStatus.OFFERED &&
                           a.getTimestamp() != null && a.getTimestamp().isAfter(thirtyDaysAgo))
                .count();
        analytics.setOffersLast30Days(offersLast30Days);

        // Calculate success rate (offers / total applications)
        if (analytics.getTotalApplications() > 0) {
            double successRate = ((double) analytics.getTotalOffers() / analytics.getTotalApplications()) * 100.0;
            analytics.setSuccessRate(successRate);
        } else {
            analytics.setSuccessRate(0.0);
        }

        // Calculate interview rate
        if (analytics.getTotalApplications() > 0) {
            double interviewRate = ((double) analytics.getTotalInterviews() / analytics.getTotalApplications()) * 100.0;
            analytics.setInterviewRate(interviewRate);
        } else {
            analytics.setInterviewRate(0.0);
        }

        // Calculate response rate (any status change from APPLIED)
        long responses = allApplications.stream()
                .filter(a -> a.getApplicationStatus() != null && 
                           a.getApplicationStatus() != ApplicationStatus.APPLIED)
                .count();
        if (analytics.getTotalApplications() > 0) {
            double responseRate = ((double) responses / analytics.getTotalApplications()) * 100.0;
            analytics.setResponseRate(responseRate);
        } else {
            analytics.setResponseRate(0.0);
        }

        // Applications by category
        Map<String, Long> applicationsByCategory = new HashMap<>();
        Map<String, Long> interviewsByCategory = new HashMap<>();
        Map<String, Long> offersByCategory = new HashMap<>();
        
        for (Applicant application : allApplications) {
            if (application.getJob() != null && application.getJob().getCategory() != null) {
                String category = application.getJob().getCategory();
                applicationsByCategory.merge(category, 1L, Long::sum);
                
                if (application.getApplicationStatus() == ApplicationStatus.INTERVIEWING) {
                    interviewsByCategory.merge(category, 1L, Long::sum);
                }
                if (application.getApplicationStatus() == ApplicationStatus.OFFERED) {
                    offersByCategory.merge(category, 1L, Long::sum);
                }
            }
        }
        analytics.setApplicationsByCategory(applicationsByCategory);

        // Top applied categories with success rates
        List<CategoryApplicationDto> topCategories = new ArrayList<>();
        for (Map.Entry<String, Long> entry : applicationsByCategory.entrySet()) {
            String category = entry.getKey();
            Long appCount = entry.getValue();
            Long interviewCount = interviewsByCategory.getOrDefault(category, 0L);
            Long offerCount = offersByCategory.getOrDefault(category, 0L);
            Double successRate = appCount > 0 ? ((double) offerCount / appCount) * 100.0 : 0.0;
            
            topCategories.add(new CategoryApplicationDto(category, appCount, interviewCount, offerCount, successRate));
        }
        topCategories.sort((a, b) -> Long.compare(b.getApplicationCount(), a.getApplicationCount()));
        analytics.setTopAppliedCategories(topCategories.stream().limit(10).collect(Collectors.toList()));

        // Calculate average response time
        List<Long> responseTimes = new ArrayList<>();
        for (Applicant application : allApplications) {
            if (application.getTimestamp() != null && 
                application.getApplicationStatus() != null &&
                application.getApplicationStatus() != ApplicationStatus.APPLIED) {
                // Approximate response time (would need status change timestamp in real system)
                // For now, use days since application
                long daysSince = ChronoUnit.DAYS.between(application.getTimestamp(), LocalDateTime.now());
                responseTimes.add(daysSince);
            }
        }
        if (!responseTimes.isEmpty()) {
            double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
            analytics.setAverageResponseTime(avgResponseTime);
        } else {
            analytics.setAverageResponseTime(0.0);
        }

        // Recent applications timeline
        List<ApplicationTimelineDto> timeline = allApplications.stream()
                .sorted((a, b) -> {
                    if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                    if (a.getTimestamp() == null) return 1;
                    if (b.getTimestamp() == null) return -1;
                    return b.getTimestamp().compareTo(a.getTimestamp());
                })
                .limit(20)
                .map(a -> {
                    ApplicationTimelineDto dto = new ApplicationTimelineDto();
                    dto.setApplicationId(a.getApplicantId());
                    if (a.getJob() != null) {
                        dto.setJobTitle(a.getJob().getJobTitle());
                        dto.setCompany(a.getJob().getCompany());
                    }
                    dto.setStatus(a.getApplicationStatus() != null ? a.getApplicationStatus().name() : "UNKNOWN");
                    dto.setAppliedAt(a.getTimestamp());
                    dto.setLastUpdatedAt(a.getTimestamp()); // Would use actual last update time in real system
                    if (a.getTimestamp() != null) {
                        dto.setDaysSinceApplication(ChronoUnit.DAYS.between(a.getTimestamp(), LocalDateTime.now()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        analytics.setRecentApplications(timeline);

        return analytics;
    }
}


