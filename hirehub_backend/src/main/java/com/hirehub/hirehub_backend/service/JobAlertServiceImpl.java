package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobAlertDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.JobAlert;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.JobStatus;
import com.hirehub.hirehub_backend.repository.JobAlertRepository;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobAlertServiceImpl implements JobAlertService {
    
    @Autowired
    private JobAlertRepository jobAlertRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Override
    public JobAlertDto createJobAlert(Long userId, JobAlertDto alertDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        JobAlert alert = alertDto.toEntity();
        alert.setUser(user);
        
        if (alert.getIsActive() == null) {
            alert.setIsActive(true);
        }
        
        if (alert.getFrequency() == null || alert.getFrequency().isEmpty()) {
            alert.setFrequency("DAILY"); // Default frequency
        }
        
        JobAlert savedAlert = jobAlertRepository.save(alert);
        return toDto(savedAlert);
    }
    
    @Override
    public JobAlertDto updateJobAlert(Long userId, Long alertId, JobAlertDto alertDto) throws Exception {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("Job alert not found"));
        
        if (!alert.getUser().getId().equals(userId)) {
            throw new Exception("You don't have permission to update this alert");
        }
        
        alert.setAlertName(alertDto.getAlertName());
        alert.setKeywords(alertDto.getKeywords());
        alert.setLocations(alertDto.getLocations());
        alert.setMinSalary(alertDto.getMinSalary());
        alert.setMaxSalary(alertDto.getMaxSalary());
        alert.setExperience(alertDto.getExperience());
        alert.setJobType(alertDto.getJobType());
        alert.setCategory(alertDto.getCategory());
        if (alertDto.getIsActive() != null) {
            alert.setIsActive(alertDto.getIsActive());
        }
        if (alertDto.getFrequency() != null) {
            alert.setFrequency(alertDto.getFrequency());
        }
        
        JobAlert updatedAlert = jobAlertRepository.save(alert);
        return toDto(updatedAlert);
    }
    
    @Override
    public void deleteJobAlert(Long userId, Long alertId) throws Exception {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("Job alert not found"));
        
        if (!alert.getUser().getId().equals(userId)) {
            throw new Exception("You don't have permission to delete this alert");
        }
        
        jobAlertRepository.delete(alert);
    }
    
    @Override
    public List<JobAlertDto> getUserJobAlerts(Long userId) {
        return jobAlertRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public JobAlertDto getJobAlertById(Long userId, Long alertId) throws Exception {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("Job alert not found"));
        
        if (!alert.getUser().getId().equals(userId)) {
            throw new Exception("You don't have permission to view this alert");
        }
        
        return toDto(alert);
    }
    
    @Override
    public void toggleJobAlert(Long userId, Long alertId) throws Exception {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("Job alert not found"));
        
        if (!alert.getUser().getId().equals(userId)) {
            throw new Exception("You don't have permission to toggle this alert");
        }
        
        alert.setIsActive(!alert.getIsActive());
        jobAlertRepository.save(alert);
    }
    
    @Override
    public List<JobDto> getMatchingJobsForAlert(Long alertId) throws Exception {
        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("Job alert not found"));
        
        // Get all open jobs
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(job -> job.getJobStatus() == JobStatus.OPEN)
                .collect(Collectors.toList());
        
        // Filter jobs based on alert criteria
        return allJobs.stream()
                .filter(job -> matchesAlertCriteria(job, alert))
                .map(Job::toDto)
                .collect(Collectors.toList());
    }
    
    private boolean matchesAlertCriteria(Job job, JobAlert alert) {
        // Check keywords
        if (alert.getKeywords() != null && !alert.getKeywords().trim().isEmpty()) {
            String keywords = alert.getKeywords().toLowerCase();
            boolean matchesKeyword = job.getJobTitle().toLowerCase().contains(keywords) ||
                    (job.getCompany() != null && job.getCompany().toLowerCase().contains(keywords)) ||
                    (job.getDescription() != null && job.getDescription().toLowerCase().contains(keywords)) ||
                    (job.getSkillsRequired() != null && job.getSkillsRequired().stream()
                            .anyMatch(skill -> skill.toLowerCase().contains(keywords)));
            if (!matchesKeyword) return false;
        }
        
        // Check location
        if (alert.getLocations() != null && !alert.getLocations().isEmpty()) {
            if (job.getLocation() == null) return false;
            boolean matchesLocation = alert.getLocations().stream()
                    .anyMatch(loc -> job.getLocation().toLowerCase().contains(loc.toLowerCase()));
            if (!matchesLocation) return false;
        }
        
        // Check salary range
        if (alert.getMinSalary() != null && job.getPackageOffered() != null) {
            if (job.getPackageOffered() < alert.getMinSalary()) return false;
        }
        if (alert.getMaxSalary() != null && job.getPackageOffered() != null) {
            if (job.getPackageOffered() > alert.getMaxSalary()) return false;
        }
        
        // Check experience
        if (alert.getExperience() != null && !alert.getExperience().trim().isEmpty()) {
            if (job.getExperience() == null || !job.getExperience().toLowerCase().contains(alert.getExperience().toLowerCase())) {
                return false;
            }
        }
        
        // Check job type
        if (alert.getJobType() != null && !alert.getJobType().trim().isEmpty()) {
            if (job.getJobType() == null || !job.getJobType().equalsIgnoreCase(alert.getJobType())) {
                return false;
            }
        }
        
        // Check category
        if (alert.getCategory() != null && !alert.getCategory().trim().isEmpty()) {
            if (job.getCategory() == null || !job.getCategory().equalsIgnoreCase(alert.getCategory())) {
                return false;
            }
        }
        
        return true;
    }
    
    private JobAlertDto toDto(JobAlert alert) {
        return new JobAlertDto(
                alert.getId(),
                alert.getUser() != null ? alert.getUser().getId() : null,
                alert.getAlertName(),
                alert.getKeywords(),
                alert.getLocations(),
                alert.getMinSalary(),
                alert.getMaxSalary(),
                alert.getExperience(),
                alert.getJobType(),
                alert.getCategory(),
                alert.getIsActive(),
                alert.getFrequency(),
                alert.getCreatedAt(),
                alert.getUpdatedAt(),
                alert.getLastSentAt()
        );
    }
}
