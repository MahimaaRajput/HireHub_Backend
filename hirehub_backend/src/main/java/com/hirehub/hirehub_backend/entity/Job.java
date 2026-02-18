package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.dto.ApplicantDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.enums.JobStatus;
import com.hirehub.hirehub_backend.enums.ShiftTiming;
import com.hirehub.hirehub_backend.enums.WorkMode;
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
    private String company; // Keep for backward compatibility
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company companyEntity; // Reference to Company entity
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
    @Enumerated(EnumType.STRING)
    private WorkMode workMode; // REMOTE, HYBRID, ON_SITE
    private LocalDateTime applicationDeadline; // Job expiry / last date to apply
    private Integer numberOfOpenings; // Number of positions to fill (null = unspecified)
    @Enumerated(EnumType.STRING)
    private ShiftTiming shiftTiming;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public JobDto toDto()
    {
        return new JobDto(this.id,this.jobTitle,this.company,this.applicants!=null?this.applicants.stream().map(Applicant::toDto).toList():null,this.about,this.experience,
                this.jobType,this.location,this.packageOffered,this.description,this.skillsRequired,this.jobStatus,this.postedBy,this.category,this.workMode,this.applicationDeadline,this.numberOfOpenings,this.shiftTiming);
    }

}
