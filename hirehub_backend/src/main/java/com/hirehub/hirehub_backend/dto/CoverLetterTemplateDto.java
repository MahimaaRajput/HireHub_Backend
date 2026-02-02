package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.CoverLetterTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverLetterTemplateDto {
    private Long id;
    private Long userId;
    private String templateName;
    private String content;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public CoverLetterTemplate toEntity() {
        CoverLetterTemplate template = new CoverLetterTemplate();
        template.setId(this.id);
        template.setTemplateName(this.templateName);
        template.setContent(this.content);
        template.setIsDefault(this.isDefault);
        // user will be set separately in service
        return template;
    }
}



