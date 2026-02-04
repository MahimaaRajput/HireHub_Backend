package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.CompanyReviewDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class CompanyReviewController {
    
    @Autowired
    private CompanyService companyService;
    
    @PostMapping("/company/{companyId}/review")
    public ResponseEntity<CompanyReviewDto> addReview(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long companyId,
            @RequestBody CompanyReviewDto reviewDto) throws Exception {
        Long reviewerId = JwtProvider.getUserIdFromToken(jwt);
        CompanyReviewDto createdReview = companyService.addReview(companyId, reviewerId, reviewDto);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }
}





