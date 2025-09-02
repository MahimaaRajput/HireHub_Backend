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
    private String email;
    private String jobTitle;
    private String location;
    private String about;
    @ElementCollection
    private List<String>skills;
    @OneToMany
    private List<Experience> experiences;
    @OneToMany
    private List<Certification> certifications;

    public ProfileDto toDto()
    {
        return new ProfileDto(this.id,this.email,this.jobTitle,this.location,this.about,this.skills,this.experiences,this.certifications);
    }

}
