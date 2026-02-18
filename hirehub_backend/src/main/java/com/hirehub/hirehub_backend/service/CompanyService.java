package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.CompanyDashboardDto;
import com.hirehub.hirehub_backend.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
    CompanyDto registerCompany(Long ownerId, CompanyDto companyDto) throws Exception;
    CompanyDto getCompanyById(Long id) throws Exception;
    CompanyDto updateCompany(Long id, CompanyDto companyDto) throws Exception;
    void verifyCompany(Long id) throws Exception;
    void rejectCompany(Long id) throws Exception;
    List<CompanyDto> getAllCompanies();
    List<CompanyDto> getCompaniesByOwner(Long ownerId);
    CompanyDashboardDto getCompanyDashboard(Long companyId) throws Exception;
    
    com.hirehub.hirehub_backend.dto.CompanyReviewDto addReview(Long companyId, Long reviewerId, com.hirehub.hirehub_backend.dto.CompanyReviewDto reviewDto) throws Exception;
    List<com.hirehub.hirehub_backend.dto.CompanyReviewDto> getCompanyReviews(Long companyId);
    Double getCompanyAverageRating(Long companyId);
}

