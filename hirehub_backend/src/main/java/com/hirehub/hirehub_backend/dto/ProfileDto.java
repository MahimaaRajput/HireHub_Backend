package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.Certification;
import com.hirehub.hirehub_backend.entity.Experience;
import com.hirehub.hirehub_backend.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private String email;
    private String jobTitle;
    private String location;
    private String about;
    private List<String> skills;
    private List<Experience> experiences;
    private List<Certification> certifications;

    public Profile toEntity()
    {
        return new Profile(this.id,this.email,this.jobTitle,this.location,this.about,this.skills,this.experiences,this.certifications);
    }
}
