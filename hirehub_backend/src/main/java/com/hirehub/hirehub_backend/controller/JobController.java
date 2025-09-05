package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/recruiter")
public class JobController {
    @Autowired
    private JobService jobService;

    @PostMapping("/post")
    public ResponseEntity<JobDto>postJob(@RequestBody @Valid JobDto jobDto)
    {
        return new ResponseEntity<>(jobService.postJob(jobDto), HttpStatus.CREATED);
    }

}
