package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.Certification;
import com.hirehub.hirehub_backend.entity.Education;
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
    private String name;
    private String email;
    private String jobTitle;
    private String location;
    private String about;
    private String photoUrl; // URL or path to profile photo
    private String resumeUrl; // URL or path to resume file (PDF/DOCX)
    private List<String> skills;
    private List<Experience> experiences;
    private List<Certification> certifications;
    private List<Education> educations;

    public Profile toEntity()
    {
        Profile profile = new Profile();
        profile.setId(this.id);
        profile.setName(this.name);
        profile.setEmail(this.email);
        profile.setJobTitle(this.jobTitle);
        profile.setLocation(this.location);
        profile.setAbout(this.about);
        profile.setPhotoUrl(this.photoUrl);
        profile.setResumeUrl(this.resumeUrl);
        profile.setSkills(this.skills);
        profile.setExperiences(this.experiences);
        profile.setCertifications(this.certifications);
        profile.setEducations(this.educations);
        return profile;
    }
}
