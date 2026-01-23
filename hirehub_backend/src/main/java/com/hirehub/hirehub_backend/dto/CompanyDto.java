package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.Company;
import com.hirehub.hirehub_backend.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
    private Long id;
    private String companyName;
    private String logoUrl;
    private String description;
    private String website;
    private String size;
    private String industry;
    private VerificationStatus verificationStatus;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Company toEntity() {
        Company company = new Company();
        company.setId(this.id);
        company.setCompanyName(this.companyName);
        company.setLogoUrl(this.logoUrl);
        company.setDescription(this.description);
        company.setWebsite(this.website);
        company.setSize(this.size);
        company.setIndustry(this.industry);
        company.setVerificationStatus(this.verificationStatus);
        // owner will be set separately in service
        return company;
    }
}

