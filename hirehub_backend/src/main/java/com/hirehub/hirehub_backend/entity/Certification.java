package com.hirehub.hirehub_backend.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Certification {
    private String issuer;
    private String title;
    private LocalDateTime issueDate;
    private String certificateID;
}
