package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;
    private LocalDateTime timestamp;
    private ApplicationStatus applicationStatus;
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
}
