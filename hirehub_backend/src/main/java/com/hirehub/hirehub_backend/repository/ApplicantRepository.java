package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    List<Applicant> findByUserEmail(String email);
    List<Applicant> findByUserId(Long userId);
    List<Applicant> findByJobId(Long jobId);
}



