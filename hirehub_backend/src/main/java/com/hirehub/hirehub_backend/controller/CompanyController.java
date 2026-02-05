package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.CompanyDashboardDto;
import com.hirehub.hirehub_backend.dto.CompanyDto;
import com.hirehub.hirehub_backend.dto.CompanyReviewDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.dto.ResponseDto;
import com.hirehub.hirehub_backend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @PostMapping("/company/register")
    public ResponseEntity<CompanyDto> registerCompany(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CompanyDto companyDto) throws Exception {
        Long ownerId = JwtProvider.getUserIdFromToken(jwt);
        CompanyDto createdCompany = companyService.registerCompany(ownerId, companyDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long companyId) throws Exception {
        CompanyDto company = companyService.getCompanyById(companyId);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }
    
    @PutMapping("/company/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long companyId,
            @RequestBody CompanyDto companyDto) throws Exception {
        CompanyDto updatedCompany = companyService.updateCompany(companyId, companyDto);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }
    
    @GetMapping("/company/dashboard/{companyId}")
    public ResponseEntity<CompanyDashboardDto> getCompanyDashboard(@PathVariable Long companyId) throws Exception {
        CompanyDashboardDto dashboard = companyService.getCompanyDashboard(companyId);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }
    
    @GetMapping("/companies/my-companies")
    public ResponseEntity<List<CompanyDto>> getMyCompanies(
            @RequestHeader("Authorization") String jwt) {
        Long ownerId = JwtProvider.getUserIdFromToken(jwt);
        List<CompanyDto> companies = companyService.getCompaniesByOwner(ownerId);
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }
    
    @PostMapping("/company/{companyId}/verify")
    public ResponseEntity<ResponseDto> verifyCompany(@PathVariable Long companyId) throws Exception {
        companyService.verifyCompany(companyId);
        return new ResponseEntity<>(new ResponseDto("Company verified successfully"), HttpStatus.OK);
    }
    
    @PostMapping("/company/{companyId}/reject")
    public ResponseEntity<ResponseDto> rejectCompany(@PathVariable Long companyId) throws Exception {
        companyService.rejectCompany(companyId);
        return new ResponseEntity<>(new ResponseDto("Company rejected"), HttpStatus.OK);
    }
    
    @GetMapping("/company/{companyId}/reviews")
    public ResponseEntity<List<CompanyReviewDto>> getCompanyReviews(@PathVariable Long companyId) {
        List<CompanyReviewDto> reviews = companyService.getCompanyReviews(companyId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
    
    @GetMapping("/company/{companyId}/average-rating")
    public ResponseEntity<Double> getCompanyAverageRating(@PathVariable Long companyId) {
        Double rating = companyService.getCompanyAverageRating(companyId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }
}






