package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDto {
    private Long id;
    private Long userId;
    private String resumeName;
    private String resumeUrl;
    private Boolean isDefault;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Resume toEntity() {
        Resume resume = new Resume();
        resume.setId(this.id);
        resume.setResumeName(this.resumeName);
        resume.setResumeUrl(this.resumeUrl);
        resume.setIsDefault(this.isDefault);
        resume.setDescription(this.description);
        // user will be set separately in service
        return resume;
    }
}







