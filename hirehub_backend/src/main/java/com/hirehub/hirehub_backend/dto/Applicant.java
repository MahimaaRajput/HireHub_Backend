package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicant {
    private Long applicationId;
    private LocalDateTime timestamp;
    private ApplicationStatus applicationStatus;
}
