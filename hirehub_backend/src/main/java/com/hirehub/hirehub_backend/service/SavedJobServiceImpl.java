package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.SavedJob;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.SavedJobRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedJobServiceImpl implements SavedJobService {
    
    @Autowired
    private SavedJobRepository savedJobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Override
    public void saveJob(Long userId, Long jobId) throws Exception {
        // Check if already saved
        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new Exception("Job is already saved");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new Exception("Job not found"));
        
        SavedJob savedJob = new SavedJob();
        savedJob.setUser(user);
        savedJob.setJob(job);
        
        savedJobRepository.save(savedJob);
    }
    
    @Override
    public void unsaveJob(Long userId, Long jobId) throws Exception {
        if (!savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new Exception("Job is not saved");
        }
        
        savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
    }
    
    @Override
    public List<JobDto> getSavedJobs(Long userId) {
        List<SavedJob> savedJobs = savedJobRepository.findByUserId(userId);
        return savedJobs.stream()
                .map(SavedJob::getJob)
                .map(Job::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isJobSaved(Long userId, Long jobId) {
        return savedJobRepository.existsByUserIdAndJobId(userId, jobId);
    }
}

