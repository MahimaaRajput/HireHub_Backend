package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.JobDto;
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
    public ResponseEntity<JobDto>postJob(@RequestBody @Valid JobDto jobDto)
    {
        return new ResponseEntity<>(jobService.postJob(jobDto), HttpStatus.CREATED);
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


}
