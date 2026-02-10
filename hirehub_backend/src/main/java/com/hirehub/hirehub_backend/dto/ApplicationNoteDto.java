package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationNoteDto {
    private Long jobId;
    private Long applicantId;
    private String notes; // Notes to add/update
}









