package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.JobAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
    List<JobAlert> findByUserId(Long userId);
    List<JobAlert> findByUserIdAndIsActiveTrue(Long userId);
    List<JobAlert> findAllByIsActiveTrue();
}

