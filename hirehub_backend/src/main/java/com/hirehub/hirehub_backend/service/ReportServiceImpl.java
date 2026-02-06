package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.Role;
import com.hirehub.hirehub_backend.repository.ApplicantRepository;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Autowired
    private RecruiterAnalyticsService recruiterAnalyticsService;

    @Autowired
    private JobSeekerAnalyticsService jobSeekerAnalyticsService;

    @Override
    public void exportAdminDashboardReport(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=admin_dashboard_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Admin Dashboard");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Metric");
            headerRow.createCell(1).setCellValue("Value");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);

            // Get dashboard data
            var dashboard = adminAnalyticsService.getAdminDashboard();
            
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Users");
            row.createCell(1).setCellValue(dashboard.getTotalUsers());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Job Seekers");
            row.createCell(1).setCellValue(dashboard.getTotalJobSeekers());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Recruiters");
            row.createCell(1).setCellValue(dashboard.getTotalRecruiters());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Jobs");
            row.createCell(1).setCellValue(dashboard.getTotalJobs());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Active Jobs");
            row.createCell(1).setCellValue(dashboard.getActiveJobs());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Applications");
            row.createCell(1).setCellValue(dashboard.getTotalApplications());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Companies");
            row.createCell(1).setCellValue(dashboard.getTotalCompanies());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Verified Companies");
            row.createCell(1).setCellValue(dashboard.getVerifiedCompanies());

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(response.getOutputStream());
        }
    }

    @Override
    public void exportUsersReport(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users_report.csv");

        List<User> users = userRepository.findAll();
        
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            // Write header
            writer.writeNext(new String[]{"ID", "Full Name", "Email", "Role", "Gender"});

            // Write data
            for (User user : users) {
                writer.writeNext(new String[]{
                    user.getId().toString(),
                    user.getFullName() != null ? user.getFullName() : "",
                    user.getEmail(),
                    user.getRole() != null ? user.getRole().name() : "",
                    user.getGender() != null ? user.getGender().name() : ""
                });
            }
        }
    }

    @Override
    public void exportJobsReport(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=jobs_report.csv");

        List<Job> jobs = jobRepository.findAll();
        
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            // Write header
            writer.writeNext(new String[]{"ID", "Job Title", "Company", "Location", "Status", "Category", "Posted By", "Created At"});

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // Write data
            for (Job job : jobs) {
                writer.writeNext(new String[]{
                    job.getId().toString(),
                    job.getJobTitle() != null ? job.getJobTitle() : "",
                    job.getCompany() != null ? job.getCompany() : "",
                    job.getLocation() != null ? job.getLocation() : "",
                    job.getJobStatus() != null ? job.getJobStatus().name() : "",
                    job.getCategory() != null ? job.getCategory() : "",
                    job.getPostedBy() != null ? job.getPostedBy() : "",
                    job.getCreatedAt() != null ? job.getCreatedAt().format(formatter) : ""
                });
            }
        }
    }

    @Override
    public void exportApplicationsReport(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=applications_report.xlsx");

        List<Applicant> applications = applicantRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Applications");
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"ID", "Name", "Email", "Job Title", "Company", "Status", "Applied At"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (Applicant app : applications) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(app.getApplicantId());
                row.createCell(1).setCellValue(app.getName() != null ? app.getName() : "");
                row.createCell(2).setCellValue(app.getEmail() != null ? app.getEmail() : "");
                row.createCell(3).setCellValue(app.getJob() != null && app.getJob().getJobTitle() != null ? app.getJob().getJobTitle() : "");
                row.createCell(4).setCellValue(app.getJob() != null && app.getJob().getCompany() != null ? app.getJob().getCompany() : "");
                row.createCell(5).setCellValue(app.getApplicationStatus() != null ? app.getApplicationStatus().name() : "");
                row.createCell(6).setCellValue(app.getTimestamp() != null ? app.getTimestamp().format(formatter) : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @Override
    public void exportRecruiterAnalyticsReport(Long recruiterId, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=recruiter_analytics_report.xlsx");

        var analytics = recruiterAnalyticsService.getRecruiterAnalytics(recruiterId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Recruiter Analytics");
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Metric");
            headerRow.createCell(1).setCellValue("Value");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Jobs Posted");
            row.createCell(1).setCellValue(analytics.getTotalJobsPosted());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Active Jobs");
            row.createCell(1).setCellValue(analytics.getActiveJobs());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Applications");
            row.createCell(1).setCellValue(analytics.getTotalApplications());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Job Views");
            row.createCell(1).setCellValue(analytics.getTotalJobViews());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(response.getOutputStream());
        }
    }

    @Override
    public void exportJobApplicationsReport(Long jobId, Long recruiterId, HttpServletResponse response) throws Exception {
        // Verify recruiter owns the job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        if (job.getPostedBy() == null || !job.getPostedBy().equals(recruiterId.toString())) {
            throw new Exception("You don't have permission to export this job's applications");
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=job_" + jobId + "_applications.csv");

        List<Applicant> applications = applicantRepository.findByJobId(jobId);
        
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(new String[]{"ID", "Name", "Email", "Phone", "Status", "Applied At", "Is Shortlisted"});

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (Applicant app : applications) {
                writer.writeNext(new String[]{
                    app.getApplicantId().toString(),
                    app.getName() != null ? app.getName() : "",
                    app.getEmail() != null ? app.getEmail() : "",
                    app.getPhoneNumber() != null ? app.getPhoneNumber().toString() : "",
                    app.getApplicationStatus() != null ? app.getApplicationStatus().name() : "",
                    app.getTimestamp() != null ? app.getTimestamp().format(formatter) : "",
                    app.getIsShortlisted() != null ? app.getIsShortlisted().toString() : "false"
                });
            }
        }
    }

    @Override
    public void exportJobSeekerAnalyticsReport(Long jobSeekerId, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=job_seeker_analytics_report.xlsx");

        var analytics = jobSeekerAnalyticsService.getJobSeekerAnalytics(jobSeekerId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Job Seeker Analytics");
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Metric");
            headerRow.createCell(1).setCellValue("Value");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Applications");
            row.createCell(1).setCellValue(analytics.getTotalApplications());

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Success Rate (%)");
            row.createCell(1).setCellValue(analytics.getSuccessRate() != null ? analytics.getSuccessRate() : 0.0);

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Interview Rate (%)");
            row.createCell(1).setCellValue(analytics.getInterviewRate() != null ? analytics.getInterviewRate() : 0.0);

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Response Rate (%)");
            row.createCell(1).setCellValue(analytics.getResponseRate() != null ? analytics.getResponseRate() : 0.0);

            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Total Offers");
            row.createCell(1).setCellValue(analytics.getTotalOffers());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(response.getOutputStream());
        }
    }

    @Override
    public void exportApplicationHistoryReport(Long jobSeekerId, HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=application_history.csv");

        List<Applicant> applications = applicantRepository.findByUserId(jobSeekerId);
        
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(new String[]{"ID", "Job Title", "Company", "Status", "Applied At"});

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (Applicant app : applications) {
                writer.writeNext(new String[]{
                    app.getApplicantId().toString(),
                    app.getJob() != null && app.getJob().getJobTitle() != null ? app.getJob().getJobTitle() : "",
                    app.getJob() != null && app.getJob().getCompany() != null ? app.getJob().getCompany() : "",
                    app.getApplicationStatus() != null ? app.getApplicationStatus().name() : "",
                    app.getTimestamp() != null ? app.getTimestamp().format(formatter) : ""
                });
            }
        }
    }
}



