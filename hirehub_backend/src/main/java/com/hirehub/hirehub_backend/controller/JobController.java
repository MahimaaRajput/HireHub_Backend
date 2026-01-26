package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ApplicationAnalyticsDto;
import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.ApplicationDto;
import com.hirehub.hirehub_backend.dto.ApplicationNoteDto;
import com.hirehub.hirehub_backend.dto.BulkApplicationActionDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
public class JobController {
    @Autowired
    private JobService jobService;

    @PostMapping("api/recruiter/post")
    public ResponseEntity<JobDto>postJob(@RequestBody @Valid JobDto jobDto,@RequestHeader("Authorization") String jwt)
    {
        Long recruiterId = JwtProvider.getUserIdFromToken(jwt);
        if (recruiterId == null) {
            throw new RuntimeException("Invalid token: userId is null");
        }
        jobDto.setPostedBy(recruiterId);
        JobDto savedJob = jobService.postJob(jobDto);
        return ResponseEntity.ok(savedJob);
    }
    @GetMapping("api/common/getall")
    public ResponseEntity<List<JobDto>> getAllJobs(@RequestParam(required = false) String sortBy)
    {
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            return new ResponseEntity<>(jobService.getAllJobsSorted(sortBy), HttpStatus.OK);
        }
        return new ResponseEntity<>(jobService.getAllJob(),HttpStatus.OK);
    }
    @GetMapping("api/common/getJobById/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(jobService.getJobById(id),HttpStatus.OK);
    }
    @PostMapping("api/user/apply/{id}")
    public ResponseEntity<String> applyJob(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id, 
            @RequestBody ApplicantDto applicantDto) throws Exception {
        // Get user ID from JWT token and set it in DTO for proper user tracking
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        applicantDto.setUserId(userId);
        return new ResponseEntity<>(jobService.applyJob(id, applicantDto), HttpStatus.OK);
    }
    @GetMapping("api/common/postedBy/{id}")
    public ResponseEntity<List<JobDto>>getJobsPostedBy(@PathVariable Long id)throws Exception
    {
        return new ResponseEntity<>(jobService.getPostedJobs(id),HttpStatus.OK);
    }
    @PostMapping("api/recruiter/changeAppStatus")
    public ResponseEntity<ResponseDto>changeAppstatus(@RequestBody ApplicationDto applicationDto)throws Exception
    {
        jobService.changeAppStatus(applicationDto);
        return new ResponseEntity<>(new ResponseDto("Application Status changed successfully!"),HttpStatus.OK);
    }

    @GetMapping("api/common/search")
    public ResponseEntity<List<JobDto>> searchJobsByKeyword(@RequestParam(required = false) String keyword) {
        List<JobDto> jobs = jobService.searchJobsByKeyword(keyword);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("api/common/filter")
    public ResponseEntity<List<JobDto>> filterJobs(
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        // Parse date strings if provided (format: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss)
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                if (startDate.length() == 10) {
                    startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
                } else {
                    startDateTime = LocalDateTime.parse(startDate);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                if (endDate.length() == 10) {
                    endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
                } else {
                    endDateTime = LocalDateTime.parse(endDate);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        
        List<JobDto> jobs = jobService.filterJobs(minSalary, maxSalary, experience, location, jobType, category, startDateTime, endDateTime);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("api/common/category/{category}")
    public ResponseEntity<List<JobDto>> getJobsByCategory(@PathVariable String category) {
        List<JobDto> jobs = jobService.getJobsByCategory(category);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("api/common/company/{company}")
    public ResponseEntity<List<JobDto>> getJobsByCompany(@PathVariable String company) {
        List<JobDto> jobs = jobService.getJobsByCompany(company);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("api/recruiter/job/{jobId}/applicants")
    public ResponseEntity<List<ApplicantDto>> getApplicantsForJob(@PathVariable Long jobId) throws Exception {
        List<ApplicantDto> applicants = jobService.getApplicantsForJob(jobId);
        return new ResponseEntity<>(applicants, HttpStatus.OK);
    }

    @PostMapping("api/recruiter/bulk-update-applications")
    public ResponseEntity<ResponseDto> bulkUpdateApplicationStatus(@RequestBody BulkApplicationActionDto bulkAction) throws Exception {
        jobService.bulkUpdateApplicationStatus(bulkAction);
        return new ResponseEntity<>(new ResponseDto("Application statuses updated successfully for " + bulkAction.getApplicantIds().size() + " applicants"), HttpStatus.OK);
    }

    @PostMapping("api/recruiter/application/notes")
    public ResponseEntity<ResponseDto> addApplicationNotes(@RequestBody ApplicationNoteDto noteDto) throws Exception {
        jobService.addApplicationNotes(noteDto);
        return new ResponseEntity<>(new ResponseDto("Application notes updated successfully"), HttpStatus.OK);
    }

    @PostMapping("api/recruiter/job/{jobId}/applicant/{applicantId}/view")
    public ResponseEntity<ResponseDto> incrementApplicationView(
            @PathVariable Long jobId,
            @PathVariable Long applicantId) throws Exception {
        jobService.incrementApplicationView(jobId, applicantId);
        return new ResponseEntity<>(new ResponseDto("View count incremented"), HttpStatus.OK);
    }

    @PostMapping("api/recruiter/job/{jobId}/applicant/{applicantId}/shortlist")
    public ResponseEntity<ResponseDto> toggleShortlistApplication(
            @PathVariable Long jobId,
            @PathVariable Long applicantId) throws Exception {
        jobService.toggleShortlistApplication(jobId, applicantId);
        return new ResponseEntity<>(new ResponseDto("Shortlist status toggled"), HttpStatus.OK);
    }

    @GetMapping("api/recruiter/job/{jobId}/analytics")
    public ResponseEntity<ApplicationAnalyticsDto> getApplicationAnalytics(@PathVariable Long jobId) throws Exception {
        ApplicationAnalyticsDto analytics = jobService.getApplicationAnalytics(jobId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }


}
