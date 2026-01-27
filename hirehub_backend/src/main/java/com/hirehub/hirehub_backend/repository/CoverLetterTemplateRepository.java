package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.CoverLetterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoverLetterTemplateRepository extends JpaRepository<CoverLetterTemplate, Long> {
    List<CoverLetterTemplate> findByUserId(Long userId);
    Optional<CoverLetterTemplate> findByUserIdAndIsDefaultTrue(Long userId);
    Optional<CoverLetterTemplate> findByIdAndUserId(Long id, Long userId);
}


