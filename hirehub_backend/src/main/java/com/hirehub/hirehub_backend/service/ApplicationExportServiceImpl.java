package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ApplicationExportServiceImpl implements ApplicationExportService {
    
    @Autowired
    private JobRepository jobRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public Resource exportApplicationsToCSV(Long jobId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        List<Applicant> applicants = job.getApplicants();
        if (applicants == null || applicants.isEmpty()) {
            throw new Exception("No applications found for this job");
        }
        
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        
        // Write header
        String[] header = {
            "Application ID", "Name", "Email", "Phone Number", 
            "Application Status", "Applied Date", "Interview Time",
            "View Count", "Is Shortlisted", "Shortlisted Date", "Recruiter Notes"
        };
        csvWriter.writeNext(header);
        
        // Write data rows
        for (Applicant applicant : applicants) {
            String[] row = {
                applicant.getApplicantId() != null ? applicant.getApplicantId().toString() : "",
                applicant.getName() != null ? applicant.getName() : "",
                applicant.getEmail() != null ? applicant.getEmail() : "",
                applicant.getPhoneNumber() != null ? applicant.getPhoneNumber().toString() : "",
                applicant.getApplicationStatus() != null ? applicant.getApplicationStatus().toString() : "",
                applicant.getTimestamp() != null ? applicant.getTimestamp().format(DATE_FORMATTER) : "",
                applicant.getInterviewTime() != null ? applicant.getInterviewTime().format(DATE_FORMATTER) : "",
                applicant.getViewCount() != null ? applicant.getViewCount().toString() : "0",
                applicant.getIsShortlisted() != null ? applicant.getIsShortlisted().toString() : "false",
                applicant.getShortlistedAt() != null ? applicant.getShortlistedAt().format(DATE_FORMATTER) : "",
                applicant.getRecruiterNotes() != null ? applicant.getRecruiterNotes() : ""
            };
            csvWriter.writeNext(row);
        }
        
        csvWriter.close();
        byte[] csvBytes = stringWriter.toString().getBytes("UTF-8");
        
        return new ByteArrayResource(csvBytes) {
            @Override
            public String getFilename() {
                return "applications_job_" + jobId + "_" + System.currentTimeMillis() + ".csv";
            }
        };
    }
    
    @Override
    public Resource exportApplicationsToExcel(Long jobId) throws Exception {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        List<Applicant> applicants = job.getApplicants();
        if (applicants == null || applicants.isEmpty()) {
            throw new Exception("No applications found for this job");
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Applications");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Application ID", "Name", "Email", "Phone Number", 
                "Application Status", "Applied Date", "Interview Time",
                "View Count", "Is Shortlisted", "Shortlisted Date", "Recruiter Notes"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Applicant applicant : applicants) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(applicant.getApplicantId() != null ? applicant.getApplicantId() : 0);
                row.createCell(1).setCellValue(applicant.getName() != null ? applicant.getName() : "");
                row.createCell(2).setCellValue(applicant.getEmail() != null ? applicant.getEmail() : "");
                row.createCell(3).setCellValue(applicant.getPhoneNumber() != null ? applicant.getPhoneNumber() : 0);
                row.createCell(4).setCellValue(applicant.getApplicationStatus() != null ? applicant.getApplicationStatus().toString() : "");
                
                if (applicant.getTimestamp() != null) {
                    row.createCell(5).setCellValue(applicant.getTimestamp().format(DATE_FORMATTER));
                } else {
                    row.createCell(5).setCellValue("");
                }
                
                if (applicant.getInterviewTime() != null) {
                    row.createCell(6).setCellValue(applicant.getInterviewTime().format(DATE_FORMATTER));
                } else {
                    row.createCell(6).setCellValue("");
                }
                
                row.createCell(7).setCellValue(applicant.getViewCount() != null ? applicant.getViewCount() : 0);
                row.createCell(8).setCellValue(applicant.getIsShortlisted() != null ? applicant.getIsShortlisted() : false);
                
                if (applicant.getShortlistedAt() != null) {
                    row.createCell(9).setCellValue(applicant.getShortlistedAt().format(DATE_FORMATTER));
                } else {
                    row.createCell(9).setCellValue("");
                }
                
                row.createCell(10).setCellValue(applicant.getRecruiterNotes() != null ? applicant.getRecruiterNotes() : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();
            
            return new ByteArrayResource(excelBytes) {
                @Override
                public String getFilename() {
                    return "applications_job_" + jobId + "_" + System.currentTimeMillis() + ".xlsx";
                }
            };
        } catch (IOException e) {
            throw new Exception("Error creating Excel file: " + e.getMessage());
        }
    }
}





