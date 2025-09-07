package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.Applicant;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.enums.JobStatus;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobTitle;
    private String company;
    private List<Applicant> applicants;
    private String about;
    private String experience;
    private String jobType;
    private String location;
    private Long packageOffered;
    private String description;
    private List<String> skillsRequired;
    private JobStatus jobStatus;

    public Job toEntity()
    {
        return new Job(this.id,this.jobTitle,this.company,this.applicants,this.about,this.experience,
                this.jobType,this.location,this.packageOffered,this.description,this.skillsRequired,this.jobStatus);
    }

}
