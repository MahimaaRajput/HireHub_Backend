package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.CoverLetterTemplateDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.CoverLetterTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class CoverLetterTemplateController {
    
    @Autowired
    private CoverLetterTemplateService templateService;
    
    @PostMapping("/cover-letter-template")
    public ResponseEntity<CoverLetterTemplateDto> createTemplate(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CoverLetterTemplateDto templateDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        CoverLetterTemplateDto createdTemplate = templateService.createTemplate(userId, templateDto);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }
    
    @PutMapping("/cover-letter-template/{templateId}")
    public ResponseEntity<CoverLetterTemplateDto> updateTemplate(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long templateId,
            @RequestBody CoverLetterTemplateDto templateDto) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        CoverLetterTemplateDto updatedTemplate = templateService.updateTemplate(userId, templateId, templateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }
    
    @DeleteMapping("/cover-letter-template/{templateId}")
    public ResponseEntity<ResponseDto> deleteTemplate(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long templateId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        templateService.deleteTemplate(userId, templateId);
        return new ResponseEntity<>(new ResponseDto("Template deleted successfully"), HttpStatus.OK);
    }
    
    @GetMapping("/cover-letter-templates")
    public ResponseEntity<List<CoverLetterTemplateDto>> getUserTemplates(
            @RequestHeader("Authorization") String jwt) {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        List<CoverLetterTemplateDto> templates = templateService.getUserTemplates(userId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }
    
    @GetMapping("/cover-letter-template/{templateId}")
    public ResponseEntity<CoverLetterTemplateDto> getTemplateById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long templateId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        CoverLetterTemplateDto template = templateService.getTemplateById(userId, templateId);
        return new ResponseEntity<>(template, HttpStatus.OK);
    }
    
    @PostMapping("/cover-letter-template/{templateId}/set-default")
    public ResponseEntity<CoverLetterTemplateDto> setDefaultTemplate(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long templateId) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        CoverLetterTemplateDto template = templateService.setDefaultTemplate(userId, templateId);
        return new ResponseEntity<>(template, HttpStatus.OK);
    }
    
    @GetMapping("/cover-letter-template/default")
    public ResponseEntity<CoverLetterTemplateDto> getDefaultTemplate(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = JwtProvider.getUserIdFromToken(jwt);
        CoverLetterTemplateDto template = templateService.getDefaultTemplate(userId);
        return new ResponseEntity<>(template, HttpStatus.OK);
    }
}






