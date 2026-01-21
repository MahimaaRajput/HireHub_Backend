package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobTitle;
    private String company;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Applicant> applicants;
    private String about;
    private String experience;
    private String jobType;
    private String location;
    private Long packageOffered;
    private String description;
    @ElementCollection
    private List<String> skillsRequired;
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;
    private Long postedBy;
    private String category; // Industry/Category of the job (e.g., IT, Finance, Healthcare, etc.)
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public JobDto toDto()
    {
        return new JobDto(this.id,this.jobTitle,this.company,this.applicants!=null?this.applicants.stream().map(Applicant::toDto).toList():null,this.about,this.experience,
                this.jobType,this.location,this.packageOffered,this.description,this.skillsRequired,this.jobStatus,this.postedBy,this.category);
    }

}
