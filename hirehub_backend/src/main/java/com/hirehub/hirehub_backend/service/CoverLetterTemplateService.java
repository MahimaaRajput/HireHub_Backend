package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.CoverLetterTemplateDto;

import java.util.List;

public interface CoverLetterTemplateService {
    CoverLetterTemplateDto createTemplate(Long userId, CoverLetterTemplateDto templateDto) throws Exception;
    CoverLetterTemplateDto updateTemplate(Long userId, Long templateId, CoverLetterTemplateDto templateDto) throws Exception;
    void deleteTemplate(Long userId, Long templateId) throws Exception;
    List<CoverLetterTemplateDto> getUserTemplates(Long userId);
    CoverLetterTemplateDto getTemplateById(Long userId, Long templateId) throws Exception;
    CoverLetterTemplateDto setDefaultTemplate(Long userId, Long templateId) throws Exception;
    CoverLetterTemplateDto getDefaultTemplate(Long userId) throws Exception;
}



