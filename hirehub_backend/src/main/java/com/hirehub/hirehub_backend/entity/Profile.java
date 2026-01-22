package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.dto.ProfileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String jobTitle;
    private String location;
    private String about;
    private String photoUrl; // URL or path to profile photo
    private String resumeUrl; // URL or path to resume file (PDF/DOCX)
    @ElementCollection
    private List<String>skills;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Experience> experiences;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Certification> certifications;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Education> educations;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Project> projects;

    public ProfileDto toDto()
    {
        return new ProfileDto(this.id,this.name,this.email,this.jobTitle,this.location,this.about,this.photoUrl,this.resumeUrl,this.skills,this.experiences,this.certifications,this.educations,this.projects);
    }

}
