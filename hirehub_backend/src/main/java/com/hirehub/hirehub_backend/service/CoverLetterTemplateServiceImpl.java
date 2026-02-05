package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.CoverLetterTemplateDto;
import com.hirehub.hirehub_backend.entity.CoverLetterTemplate;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.CoverLetterTemplateRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoverLetterTemplateServiceImpl implements CoverLetterTemplateService {
    
    @Autowired
    private CoverLetterTemplateRepository templateRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public CoverLetterTemplateDto createTemplate(Long userId, CoverLetterTemplateDto templateDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        CoverLetterTemplate template = templateDto.toEntity();
        template.setUser(user);
        
        // If this is set as default, unset other default templates
        if (templateDto.getIsDefault() != null && templateDto.getIsDefault()) {
            templateRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existingDefault -> {
                existingDefault.setIsDefault(false);
                templateRepository.save(existingDefault);
            });
        } else {
            // If no default exists, make this one default
            if (templateRepository.findByUserIdAndIsDefaultTrue(userId).isEmpty()) {
                template.setIsDefault(true);
            } else {
                template.setIsDefault(false);
            }
        }
        
        CoverLetterTemplate savedTemplate = templateRepository.save(template);
        return toDto(savedTemplate);
    }
    
    @Override
    public CoverLetterTemplateDto updateTemplate(Long userId, Long templateId, CoverLetterTemplateDto templateDto) throws Exception {
        CoverLetterTemplate template = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new Exception("Template not found or you don't have permission"));
        
        template.setTemplateName(templateDto.getTemplateName());
        template.setContent(templateDto.getContent());
        
        // Handle default flag
        if (templateDto.getIsDefault() != null && templateDto.getIsDefault() && !template.getIsDefault()) {
            // Unset other defaults
            templateRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existingDefault -> {
                existingDefault.setIsDefault(false);
                templateRepository.save(existingDefault);
            });
            template.setIsDefault(true);
        }
        
        CoverLetterTemplate updatedTemplate = templateRepository.save(template);
        return toDto(updatedTemplate);
    }
    
    @Override
    public void deleteTemplate(Long userId, Long templateId) throws Exception {
        CoverLetterTemplate template = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new Exception("Template not found or you don't have permission"));
        
        // If deleting default template, set another one as default
        if (template.getIsDefault()) {
            List<CoverLetterTemplate> otherTemplates = templateRepository.findByUserId(userId).stream()
                    .filter(t -> !t.getId().equals(templateId))
                    .collect(Collectors.toList());
            
            if (!otherTemplates.isEmpty()) {
                otherTemplates.get(0).setIsDefault(true);
                templateRepository.save(otherTemplates.get(0));
            }
        }
        
        templateRepository.delete(template);
    }
    
    @Override
    public List<CoverLetterTemplateDto> getUserTemplates(Long userId) {
        return templateRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CoverLetterTemplateDto getTemplateById(Long userId, Long templateId) throws Exception {
        CoverLetterTemplate template = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new Exception("Template not found or you don't have permission"));
        return toDto(template);
    }
    
    @Override
    public CoverLetterTemplateDto setDefaultTemplate(Long userId, Long templateId) throws Exception {
        CoverLetterTemplate template = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new Exception("Template not found or you don't have permission"));
        
        // Unset other defaults
        templateRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existingDefault -> {
            existingDefault.setIsDefault(false);
            templateRepository.save(existingDefault);
        });
        
        template.setIsDefault(true);
        CoverLetterTemplate updatedTemplate = templateRepository.save(template);
        return toDto(updatedTemplate);
    }
    
    @Override
    public CoverLetterTemplateDto getDefaultTemplate(Long userId) throws Exception {
        CoverLetterTemplate template = templateRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new Exception("No default template found"));
        return toDto(template);
    }
    
    private CoverLetterTemplateDto toDto(CoverLetterTemplate template) {
        return new CoverLetterTemplateDto(
                template.getId(),
                template.getUser() != null ? template.getUser().getId() : null,
                template.getTemplateName(),
                template.getContent(),
                template.getIsDefault(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}






