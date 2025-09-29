package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicantId;
    private String name;
    private String email;
    private Long phoneNumber;
    @Lob
    private byte[] resume;
    private String coverLetter;
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;
    private LocalDateTime interviewTime;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    public ApplicantDto toDto(){
        return new ApplicantDto(this.applicantId,this.name,this.email,this.phoneNumber,this.resume!=null? Base64.getEncoder().encodeToString(this.resume):null,this.coverLetter,this.timestamp,this.applicationStatus, this.interviewTime);
    }
}
