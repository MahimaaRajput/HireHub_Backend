package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDto {
    private Long applicantId;
    private String name;
    private String email;
    private Long phoneNumber;
    private String resume;
    private String coverLetter;
    private LocalDateTime timestamp;
    private ApplicationStatus applicationStatus;
    private LocalDateTime interviewTime;
    private Long userId; // User ID who applied (for better tracking)
    private String recruiterNotes; // Notes added by recruiter (private, not visible to applicant)

    public Applicant toEntity(){
        Applicant applicant = new Applicant();
        applicant.setApplicantId(this.applicantId);
        applicant.setName(this.name);
        applicant.setEmail(this.email);
        applicant.setPhoneNumber(this.phoneNumber);
        // Convert Base64 string to byte array
        applicant.setResume(this.resume != null ? Base64.getDecoder().decode(this.resume) : null);
        applicant.setCoverLetter(this.coverLetter);
        applicant.setTimestamp(this.timestamp);
        applicant.setApplicationStatus(this.applicationStatus);
        applicant.setInterviewTime(this.interviewTime);
        applicant.setRecruiterNotes(this.recruiterNotes);
        // Job will be set separately in service layer
        return applicant;
    }
}
