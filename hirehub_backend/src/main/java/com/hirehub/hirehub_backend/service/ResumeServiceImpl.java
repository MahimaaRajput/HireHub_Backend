package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ResumeDto;
import com.hirehub.hirehub_backend.entity.Resume;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.ResumeRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {
    
    @Autowired
    private ResumeRepository resumeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public ResumeDto createResume(Long userId, ResumeDto resumeDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        Resume resume = resumeDto.toEntity();
        resume.setUser(user);
        
        // If this is set as default, unset other default resumes
        if (resumeDto.getIsDefault() != null && resumeDto.getIsDefault()) {
            resumeRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existingDefault -> {
                existingDefault.setIsDefault(false);
                resumeRepository.save(existingDefault);
            });
        } else {
            // If no default exists, make this one default
            if (resumeRepository.findByUserIdAndIsDefaultTrue(userId).isEmpty()) {
                resume.setIsDefault(true);
            } else {
                resume.setIsDefault(false);
            }
        }
        
        Resume savedResume = resumeRepository.save(resume);
        return toDto(savedResume);
    }
    
    @Override
    public ResumeDto updateResume(Long userId, Long resumeId, ResumeDto resumeDto) throws Exception {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new Exception("Resume not found or you don't have permission"));
        
        resume.setResumeName(resumeDto.getResumeName());
        resume.setResumeUrl(resumeDto.getResumeUrl());
        resume.setDescription(resumeDto.getDescription());
        
        // Handle default flag
        if (resumeDto.getIsDefault() != null && resumeDto.getIsDefault() && !resume.getIsDefault()) {
            // Unset other defaults
            resumeRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existingDefault -> {
                existingDefault.setIsDefault(false);
                resumeRepository.save(existingDefault);
            });
            resume.setIsDefault(true);
        }
        
        Resume updatedResume = resumeRepository.save(resume);
        return toDto(updatedResume);
    }
    
    @Override
    public void deleteResume(Long userId, Long resumeId) throws Exception {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new Exception("Resume not found or you don't have permission"));
        
        // If deleting default resume, set another one as default
        if (resume.getIsDefault()) {
            List<Resume> otherResumes = resumeRepository.findByUserId(userId).stream()
                    .filter(r -> !r.getId().equals(resumeId))
                    .collect(Collectors.toList());
            
            if (!otherResumes.isEmpty()) {
                otherResumes.get(0).setIsDefault(true);
                resumeRepository.save(otherResumes.get(0));
            }
        }
        
        resumeRepository.delete(resume);
    }
    
    @Override
    public List<ResumeDto> getUserResumes(Long userId) {
        return resumeRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ResumeDto getResumeById(Long userId, Long resumeId) throws Exception {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new Exception("Resume not found or you don't have permission"));
        return toDto(resume);
    }
    
    @Override
    public ResumeDto setDefaultResume(Long userId, Long resumeId) throws Exception {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new Exception("Resume not found or you don't have permission"));
        
        // Unset other defaults
        resumeRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existingDefault -> {
            existingDefault.setIsDefault(false);
            resumeRepository.save(existingDefault);
        });
        
        resume.setIsDefault(true);
        Resume updatedResume = resumeRepository.save(resume);
        return toDto(updatedResume);
    }
    
    @Override
    public ResumeDto getDefaultResume(Long userId) throws Exception {
        Resume resume = resumeRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new Exception("No default resume found"));
        return toDto(resume);
    }
    
    private ResumeDto toDto(Resume resume) {
        return new ResumeDto(
                resume.getId(),
                resume.getUser() != null ? resume.getUser().getId() : null,
                resume.getResumeName(),
                resume.getResumeUrl(),
                resume.getIsDefault(),
                resume.getDescription(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }
}





