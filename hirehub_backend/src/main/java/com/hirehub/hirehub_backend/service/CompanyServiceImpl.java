package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.CompanyDto;
import com.hirehub.hirehub_backend.entity.Company;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.VerificationStatus;
import com.hirehub.hirehub_backend.repository.CompanyRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public CompanyDto registerCompany(Long ownerId, CompanyDto companyDto) throws Exception {
        // Check if company name already exists
        if (companyRepository.findByCompanyName(companyDto.getCompanyName()).isPresent()) {
            throw new Exception("Company with this name already exists");
        }
        
        // Get owner user
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new Exception("User not found"));
        
        // Create company entity
        Company company = companyDto.toEntity();
        company.setOwner(owner);
        company.setVerificationStatus(VerificationStatus.PENDING); // Default to PENDING
        
        Company savedCompany = companyRepository.save(company);
        return savedCompany.toDto();
    }
    
    @Override
    public CompanyDto getCompanyById(Long id) throws Exception {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new Exception("Company not found"));
        return company.toDto();
    }
    
    @Override
    public CompanyDto updateCompany(Long id, CompanyDto companyDto) throws Exception {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new Exception("Company not found"));
        
        // Update fields
        company.setLogoUrl(companyDto.getLogoUrl());
        company.setDescription(companyDto.getDescription());
        company.setWebsite(companyDto.getWebsite());
        company.setSize(companyDto.getSize());
        company.setIndustry(companyDto.getIndustry());
        
        Company updatedCompany = companyRepository.save(company);
        return updatedCompany.toDto();
    }
    
    @Override
    public void verifyCompany(Long id) throws Exception {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new Exception("Company not found"));
        company.setVerificationStatus(VerificationStatus.VERIFIED);
        companyRepository.save(company);
    }
    
    @Override
    public void rejectCompany(Long id) throws Exception {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new Exception("Company not found"));
        company.setVerificationStatus(VerificationStatus.REJECTED);
        companyRepository.save(company);
    }
    
    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(Company::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CompanyDto> getCompaniesByOwner(Long ownerId) {
        return companyRepository.findByOwnerId(ownerId).stream()
                .map(Company::toDto)
                .collect(Collectors.toList());
    }
}

