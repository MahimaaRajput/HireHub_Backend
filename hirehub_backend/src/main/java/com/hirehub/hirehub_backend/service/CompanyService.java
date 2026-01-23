package com.hirehub.hirehub_backend.service;

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
}

