package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ResumeDto;

import java.util.List;

public interface ResumeService {
    ResumeDto createResume(Long userId, ResumeDto resumeDto) throws Exception;
    ResumeDto updateResume(Long userId, Long resumeId, ResumeDto resumeDto) throws Exception;
    void deleteResume(Long userId, Long resumeId) throws Exception;
    List<ResumeDto> getUserResumes(Long userId);
    ResumeDto getResumeById(Long userId, Long resumeId) throws Exception;
    ResumeDto setDefaultResume(Long userId, Long resumeId) throws Exception;
    ResumeDto getDefaultResume(Long userId) throws Exception;
}






