package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyName(String companyName);
    List<Company> findByOwnerId(Long ownerId);
    List<Company> findByIndustry(String industry);
}






