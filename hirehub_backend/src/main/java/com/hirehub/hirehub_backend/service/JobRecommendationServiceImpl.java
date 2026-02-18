package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.JobView;
import com.hirehub.hirehub_backend.enums.JobStatus;
import com.hirehub.hirehub_backend.entity.Profile;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.JobViewRepository;
import com.hirehub.hirehub_backend.repository.ProfileRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobRecommendationServiceImpl implements JobRecommendationService {
    
    @Autowired
    private JobViewRepository jobViewRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Override
    @Transactional
    public void recordJobView(Long userId, Long jobId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        Optional<JobView> existingView = jobViewRepository.findByUserIdAndJobId(userId, jobId);
        
        if (existingView.isPresent()) {
            // Update existing view - increment count and update timestamp
            JobView jobView = existingView.get();
            jobView.setViewCount(jobView.getViewCount() != null ? jobView.getViewCount() + 1 : 1);
            jobView.setViewedAt(LocalDateTime.now());
            jobViewRepository.save(jobView);
        } else {
            // Create new view record
            JobView jobView = new JobView();
            jobView.setUser(user);
            jobView.setJob(job);
            jobView.setViewCount(1);
            jobViewRepository.save(jobView);
        }
    }
    
    @Override
    public List<JobDto> getRecentlyViewedJobs(Long userId, Integer limit) throws Exception {
        int viewLimit = limit != null && limit > 0 ? limit : 10;
        
        List<JobView> jobViews = jobViewRepository.findRecentViewsByUserId(userId);
        
        // Limit results and extract unique jobs (most recent view per job)
        Map<Long, JobView> uniqueJobs = new LinkedHashMap<>();
        for (JobView view : jobViews) {
            if (uniqueJobs.size() >= viewLimit) break;
            if (!uniqueJobs.containsKey(view.getJob().getId())) {
                uniqueJobs.put(view.getJob().getId(), view);
            }
        }
        
        return uniqueJobs.values().stream()
                .map(jobView -> jobView.getJob().toDto())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobDto> getPopularJobsInArea(String location, Integer limit) throws Exception {
        int jobLimit = limit != null && limit > 0 ? limit : 20;
        
        // Get all jobs in the area
        List<Job> jobsInArea = jobRepository.findByLocationContainingIgnoreCase(location);
        
        if (jobsInArea.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Calculate popularity score for each job
        // Popularity = (view count * 0.4) + (application count * 0.6)
        Map<Job, Double> jobScores = new HashMap<>();
        
        for (Job job : jobsInArea) {
            Long viewCount = jobViewRepository.countViewsByJobId(job.getId());
            Long applicationCount = job.getApplicants() != null ? (long) job.getApplicants().size() : 0L;
            
            double score = (viewCount * 0.4) + (applicationCount * 0.6);
            jobScores.put(job, score);
        }
        
        // Sort by score descending and return top jobs
        return jobScores.entrySet().stream()
                .sorted(Map.Entry.<Job, Double>comparingByValue().reversed())
                .limit(jobLimit)
                .map(entry -> entry.getKey().toDto())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobDto> getSimilarJobs(Long jobId, Integer limit) throws Exception {
        Job sourceJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        int jobLimit = limit != null && limit > 0 ? limit : 10;
        
        // Get all active jobs except the source job
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(job -> !job.getId().equals(jobId) && JobStatus.OPEN.equals(job.getJobStatus()))
                .collect(Collectors.toList());
        
        // Calculate similarity score for each job
        Map<Job, Double> similarityScores = new HashMap<>();
        
        for (Job job : allJobs) {
            double score = calculateSimilarityScore(sourceJob, job);
            if (score > 0) {
                similarityScores.put(job, score);
            }
        }
        
        // Sort by similarity score descending and return top jobs
        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Job, Double>comparingByValue().reversed())
                .limit(jobLimit)
                .map(entry -> entry.getKey().toDto())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobDto> getJobRecommendationsBasedOnProfile(Long userId, Integer limit) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        Profile profile = user.getEmail() != null
                ? profileRepository.findByEmail(user.getEmail()).orElse(null)
                : null;
        
        if (profile == null) {
            return new ArrayList<>();
        }
        
        int jobLimit = limit != null && limit > 0 ? limit : 20;
        
        // Get all active jobs
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(job -> JobStatus.OPEN.equals(job.getJobStatus()))
                .collect(Collectors.toList());
        
        // Calculate match score for each job based on profile
        Map<Job, Double> matchScores = new HashMap<>();
        
        for (Job job : allJobs) {
            double score = calculateProfileMatchScore(profile, job);
            if (score > 0) {
                matchScores.put(job, score);
            }
        }
        
        // Sort by match score descending and return top jobs
        return matchScores.entrySet().stream()
                .sorted(Map.Entry.<Job, Double>comparingByValue().reversed())
                .limit(jobLimit)
                .map(entry -> entry.getKey().toDto())
                .collect(Collectors.toList());
    }
    
    private double calculateSimilarityScore(Job job1, Job job2) {
        double score = 0.0;
        double maxScore = 0.0;
        
        // Category match (30% weight)
        if (job1.getCategory() != null && job2.getCategory() != null) {
            maxScore += 0.3;
            if (job1.getCategory().equalsIgnoreCase(job2.getCategory())) {
                score += 0.3;
            }
        }
        
        // Location match (20% weight)
        if (job1.getLocation() != null && job2.getLocation() != null) {
            maxScore += 0.2;
            if (job1.getLocation().equalsIgnoreCase(job2.getLocation())) {
                score += 0.2;
            } else if (job1.getLocation().toLowerCase().contains(job2.getLocation().toLowerCase()) ||
                      job2.getLocation().toLowerCase().contains(job1.getLocation().toLowerCase())) {
                score += 0.1; // Partial match
            }
        }
        
        // Skills match (30% weight)
        if (job1.getSkillsRequired() != null && job2.getSkillsRequired() != null) {
            maxScore += 0.3;
            Set<String> skills1 = new HashSet<>(job1.getSkillsRequired());
            Set<String> skills2 = new HashSet<>(job2.getSkillsRequired());
            long commonSkills = skills1.stream().filter(skills2::contains).count();
            if (skills1.size() > 0) {
                score += 0.3 * (commonSkills / (double) Math.max(skills1.size(), skills2.size()));
            }
        }
        
        // Job type match (10% weight)
        if (job1.getJobType() != null && job2.getJobType() != null) {
            maxScore += 0.1;
            if (job1.getJobType().equalsIgnoreCase(job2.getJobType())) {
                score += 0.1;
            }
        }
        
        // Experience match (10% weight) - Job has experience (String), not experienceLevel
        if (job1.getExperience() != null && job2.getExperience() != null) {
            maxScore += 0.1;
            if (job1.getExperience().equalsIgnoreCase(job2.getExperience())) {
                score += 0.1;
            }
        }
        
        // Normalize score to 0-1 range
        return maxScore > 0 ? score / maxScore : 0.0;
    }
    
    private double calculateProfileMatchScore(Profile profile, Job job) {
        double score = 0.0;
        double maxScore = 0.0;
        
        // Skills match (40% weight) - most important
        if (profile.getSkills() != null && !profile.getSkills().isEmpty() && 
            job.getSkillsRequired() != null && !job.getSkillsRequired().isEmpty()) {
            maxScore += 0.4;
            Set<String> profileSkills = new HashSet<>(profile.getSkills());
            Set<String> jobSkills = new HashSet<>(job.getSkillsRequired());
            long matchingSkills = profileSkills.stream().filter(jobSkills::contains).count();
            if (jobSkills.size() > 0) {
                score += 0.4 * (matchingSkills / (double) jobSkills.size());
            }
        }
        
        // Preferred location match (25% weight)
        if (profile.getPreferredLocations() != null && !profile.getPreferredLocations().isEmpty() && 
            job.getLocation() != null) {
            maxScore += 0.25;
            boolean locationMatch = profile.getPreferredLocations().stream()
                    .anyMatch(loc -> job.getLocation().toLowerCase().contains(loc.toLowerCase()) ||
                                   loc.toLowerCase().contains(job.getLocation().toLowerCase()));
            if (locationMatch) {
                score += 0.25;
            }
        }
        
        // Experience match (15% weight) - based on experiences in profile; Job has experience (String)
        if (profile.getExperiences() != null && !profile.getExperiences().isEmpty() && 
            job.getExperience() != null) {
            maxScore += 0.15;
            String jobExp = job.getExperience().toLowerCase();
            boolean experienceMatch = profile.getExperiences().stream()
                    .anyMatch(exp -> exp.getTitle() != null && 
                            exp.getTitle().toLowerCase().contains(jobExp));
            if (experienceMatch) {
                score += 0.15;
            }
        }
        
        // Expected salary vs offered package (10% weight)
        if (profile.getExpectedSalary() != null && job.getPackageOffered() != null) {
            maxScore += 0.1;
            if (job.getPackageOffered() >= profile.getExpectedSalary() * 0.9) {
                score += 0.1; // Job pays at least 90% of expected
            } else if (job.getPackageOffered() >= profile.getExpectedSalary() * 0.7) {
                score += 0.05; // Job pays at least 70% of expected
            }
        }
        
        // Job type match (10% weight) - can be inferred from profile or skipped if not available
        // This is optional as Profile doesn't have explicit jobType field
        
        // Normalize score to 0-1 range
        return maxScore > 0 ? score / maxScore : 0.0;
    }
}

