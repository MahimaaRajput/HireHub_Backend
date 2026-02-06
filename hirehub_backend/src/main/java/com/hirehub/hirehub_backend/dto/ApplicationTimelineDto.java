package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTimelineDto {
    private Long applicationId;
    private String jobTitle;
    private String company;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime lastUpdatedAt;
    private Long daysSinceApplication;
}



