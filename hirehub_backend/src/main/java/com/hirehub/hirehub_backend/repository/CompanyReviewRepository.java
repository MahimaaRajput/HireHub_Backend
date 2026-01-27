package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.CompanyReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyReviewRepository extends JpaRepository<CompanyReview, Long> {
    List<CompanyReview> findByCompanyId(Long companyId);
    List<CompanyReview> findByReviewerId(Long reviewerId);
    boolean existsByCompanyIdAndReviewerId(Long companyId, Long reviewerId);
}


