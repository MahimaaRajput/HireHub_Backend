package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationHistoryDto {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String company;
    private String location;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
    private LocalDateTime interviewTime;
    private String coverLetter;
}


