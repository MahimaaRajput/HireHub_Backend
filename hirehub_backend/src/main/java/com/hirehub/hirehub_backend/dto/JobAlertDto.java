package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.JobAlert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAlertDto {
    private Long id;
    private Long userId;
    private String alertName;
    private String keywords;
    private List<String> locations;
    private Long minSalary;
    private Long maxSalary;
    private String experience;
    private String jobType;
    private String category;
    private Boolean isActive;
    private String frequency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSentAt;
    
    public JobAlert toEntity() {
        JobAlert alert = new JobAlert();
        alert.setId(this.id);
        alert.setAlertName(this.alertName);
        alert.setKeywords(this.keywords);
        alert.setLocations(this.locations);
        alert.setMinSalary(this.minSalary);
        alert.setMaxSalary(this.maxSalary);
        alert.setExperience(this.experience);
        alert.setJobType(this.jobType);
        alert.setCategory(this.category);
        alert.setIsActive(this.isActive != null ? this.isActive : true);
        alert.setFrequency(this.frequency);
        alert.setLastSentAt(this.lastSentAt);
        // user will be set separately in service
        return alert;
    }
}
