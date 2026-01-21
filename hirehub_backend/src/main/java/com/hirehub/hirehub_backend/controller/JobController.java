package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.ApplicationDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<JobDto>> getAllJobs()
    {
        return new ResponseEntity<>(jobService.getAllJob(),HttpStatus.OK);
    }
    @GetMapping("api/common/getJobById/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(jobService.getJobById(id),HttpStatus.OK);
    }
    @PostMapping("api/user/apply/{id}")
      public ResponseEntity<String> applyJob(@PathVariable Long id, @RequestBody ApplicantDto applicantDto) throws Exception {
        return new ResponseEntity<>(jobService.applyJob(id,applicantDto),HttpStatus.OK);
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


}
