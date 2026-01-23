package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.CompanyDashboardDto;
import com.hirehub.hirehub_backend.dto.CompanyDto;
import com.hirehub.hirehub_backend.dto.CompanyReviewDto;
import com.hirehub.hirehub_backend.dto.JobDto;
import com.hirehub.hirehub_backend.entity.Company;
import com.hirehub.hirehub_backend.entity.CompanyReview;
import com.hirehub.hirehub_backend.entity.Job;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.ApplicationStatus;
import com.hirehub.hirehub_backend.enums.JobStatus;
import com.hirehub.hirehub_backend.enums.VerificationStatus;
import com.hirehub.hirehub_backend.repository.CompanyRepository;
import com.hirehub.hirehub_backend.repository.CompanyReviewRepository;
import com.hirehub.hirehub_backend.repository.JobRepository;
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
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private CompanyReviewRepository companyReviewRepository;
    
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
    
    @Override
    public CompanyDashboardDto getCompanyDashboard(Long companyId) throws Exception {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new Exception("Company not found"));
        
        // Get all jobs for this company
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(job -> job.getCompanyEntity() != null && job.getCompanyEntity().getId().equals(companyId))
                .collect(Collectors.toList());
        
        // Calculate statistics
        long totalJobsPosted = allJobs.size();
        long activeJobsCount = allJobs.stream()
                .filter(job -> job.getJobStatus() == JobStatus.OPEN)
                .count();
        
        long totalApplications = allJobs.stream()
                .mapToLong(job -> job.getApplicants() != null ? job.getApplicants().size() : 0)
                .sum();
        
        long pendingApplications = allJobs.stream()
                .flatMap(job -> job.getApplicants() != null ? job.getApplicants().stream() : java.util.stream.Stream.empty())
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.APPLIED)
                .count();
        
        long interviewingApplications = allJobs.stream()
                .flatMap(job -> job.getApplicants() != null ? job.getApplicants().stream() : java.util.stream.Stream.empty())
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.INTERVIEWING)
                .count();
        
        long offeredApplications = allJobs.stream()
                .flatMap(job -> job.getApplicants() != null ? job.getApplicants().stream() : java.util.stream.Stream.empty())
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.OFFERED)
                .count();
        
        // Get recent jobs (last 5, sorted by creation date)
        List<JobDto> recentJobs = allJobs.stream()
                .sorted((j1, j2) -> j2.getCreatedAt().compareTo(j1.getCreatedAt()))
                .limit(5)
                .map(Job::toDto)
                .collect(Collectors.toList());
        
        return new CompanyDashboardDto(
                company.toDto(),
                totalJobsPosted,
                activeJobsCount,
                totalApplications,
                pendingApplications,
                interviewingApplications,
                offeredApplications,
                recentJobs
        );
    }
    
    @Override
    public CompanyReviewDto addReview(Long companyId, Long reviewerId, CompanyReviewDto reviewDto) throws Exception {
        // Check if company exists
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new Exception("Company not found"));
        
        // Check if reviewer exists
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new Exception("Reviewer not found"));
        
        // Check if user has already reviewed this company
        if (companyReviewRepository.existsByCompanyIdAndReviewerId(companyId, reviewerId)) {
            throw new Exception("You have already reviewed this company");
        }
        
        // Validate rating
        if (reviewDto.getRating() == null || reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            throw new Exception("Rating must be between 1 and 5");
        }
        
        // Create review
        CompanyReview review = reviewDto.toEntity();
        review.setCompany(company);
        review.setReviewer(reviewer);
        
        CompanyReview savedReview = companyReviewRepository.save(review);
        return savedReview.toDto();
    }
    
    @Override
    public List<CompanyReviewDto> getCompanyReviews(Long companyId) {
        return companyReviewRepository.findByCompanyId(companyId).stream()
                .map(CompanyReview::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Double getCompanyAverageRating(Long companyId) {
        List<CompanyReview> reviews = companyReviewRepository.findByCompanyId(companyId);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double average = reviews.stream()
                .mapToInt(CompanyReview::getRating)
                .average()
                .orElse(0.0);
        
        return Math.round(average * 10.0) / 10.0; // Round to 1 decimal place
    }
}

