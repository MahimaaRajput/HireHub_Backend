package com.hirehub.hirehub_backend.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {
    // Admin reports
    void exportAdminDashboardReport(HttpServletResponse response) throws Exception;
    void exportUsersReport(HttpServletResponse response) throws Exception;
    void exportJobsReport(HttpServletResponse response) throws Exception;
    void exportApplicationsReport(HttpServletResponse response) throws Exception;

    // Recruiter reports
    void exportRecruiterAnalyticsReport(Long recruiterId, HttpServletResponse response) throws Exception;
    void exportJobApplicationsReport(Long jobId, Long recruiterId, HttpServletResponse response) throws Exception;

    // Job seeker reports
    void exportJobSeekerAnalyticsReport(Long jobSeekerId, HttpServletResponse response) throws Exception;
    void exportApplicationHistoryReport(Long jobSeekerId, HttpServletResponse response) throws Exception;
}

