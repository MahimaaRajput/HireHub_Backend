package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.service.ReportService;
import com.hirehub.hirehub_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    // Admin report endpoints
    @GetMapping("/admin/reports/dashboard")
    public void exportAdminDashboardReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        reportService.exportAdminDashboardReport(response);
    }

    @GetMapping("/admin/reports/users")
    public void exportUsersReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        reportService.exportUsersReport(response);
    }

    @GetMapping("/admin/reports/jobs")
    public void exportJobsReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        reportService.exportJobsReport(response);
    }

    @GetMapping("/admin/reports/applications")
    public void exportApplicationsReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        reportService.exportApplicationsReport(response);
    }

    // Recruiter report endpoints
    @GetMapping("/recruiter/reports/analytics")
    public void exportRecruiterAnalyticsReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        reportService.exportRecruiterAnalyticsReport(recruiterId, response);
    }

    @GetMapping("/recruiter/reports/job/{jobId}/applications")
    public void exportJobApplicationsReport(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long jobId,
            HttpServletResponse response) throws Exception {
        Long recruiterId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        reportService.exportJobApplicationsReport(jobId, recruiterId, response);
    }

    // Job seeker report endpoints
    @GetMapping("/user/reports/analytics")
    public void exportJobSeekerAnalyticsReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        Long jobSeekerId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        reportService.exportJobSeekerAnalyticsReport(jobSeekerId, response);
    }

    @GetMapping("/user/reports/application-history")
    public void exportApplicationHistoryReport(
            @RequestHeader("Authorization") String jwt,
            HttpServletResponse response) throws Exception {
        Long jobSeekerId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        reportService.exportApplicationHistoryReport(jobSeekerId, response);
    }
}



