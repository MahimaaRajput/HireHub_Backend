package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkApplicationActionDto {
    private Long jobId;
    private List<Long> applicantIds; // List of applicant IDs to update
    private ApplicationStatus newStatus; // New status to apply to all selected applicants
}







